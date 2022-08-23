terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "4.23.0"
    }
  }
}

provider "aws" {
  region     = "eu-west-2"
  access_key = var.aws_access_key
  secret_key = var.aws_secret_key
}

variable "aws_access_key" {
  description = "AWS Access Key"
  type        = string
}
variable "aws_secret_key" {
  description = "AWS Access Key Secret"
  type        = string
}

variable "github_secret_key" {
  description = "Github API Key"
  type        = string
}
variable "github_username" {
  description = "Github Username"
  type        = string
}
variable "github_repo" {
  description = "Github repo that the cv data is stored in."
  type        = string
}

variable "function_jar" {
  description = "Path to shadowjar file"
  type        = string
  default     = "./build/libs/shadow.jar"
}

variable "name" {
  description = "The name of the Lambda function"
  type        = string
  default     = "cv-server"
}

resource "aws_iam_role" "lambda-exec" {
  name = "${var.name}_exec_role"

  # Terraform's "jsonencode" function converts a Terraform expression result to valid JSON syntax.
  assume_role_policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [
      {
        Action    = "sts:AssumeRole"
        Effect    = "Allow"
        Sid       = ""
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      },
    ]
  })
}

resource "aws_lambda_function" "lambda" {
  architectures    = ["x86_64"]
  filename         = var.function_jar
  source_code_hash = filebase64sha256(var.function_jar)
  function_name    = "${var.name}_lambda"
  role             = aws_iam_role.lambda-exec.arn
  handler          = "dev.markstanden.DataLookup::handleRequest"
  runtime          = "java11"
  timeout          = 20
  environment {
    variables = {
      PERSONAL_ACCESS_TOKEN = var.github_secret_key
      USER_NAME = var.github_username
      REPO_NAME = var.github_repo
    }
  }
}


resource "aws_iam_role_policy_attachment" "lambda_policy" {
  role       = aws_iam_role.lambda-exec.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}
resource "aws_cloudwatch_log_group" "lambda-log" {
  name              = "/aws/lambda/${aws_lambda_function.lambda.function_name}"
  retention_in_days = 7
}

resource "aws_apigatewayv2_api" "lambda" {
  name          = "${var.name}_gateway"
  protocol_type = "HTTP"
  cors_configuration {
    allow_origins = ["https://api.github.com"]
    allow_methods = ["POST", "GET"]
    allow_headers = ["content-type", "Accept", "Authorization"]
    max_age = 300
  }
}

resource "aws_apigatewayv2_stage" "lambda" {
  api_id      = aws_apigatewayv2_api.lambda.id
  name        = "${var.name}_stage"
  auto_deploy = true

  access_log_settings {
    destination_arn = aws_cloudwatch_log_group.api_gw.arn

    format = jsonencode({
      requestId               = "$context.requestId"
      sourceIp                = "$context.identity.sourceIp"
      requestTime             = "$context.requestTime"
      protocol                = "$context.protocol"
      httpMethod              = "$context.httpMethod"
      resourcePath            = "$context.resourcePath"
      routeKey                = "$context.routeKey"
      status                  = "$context.status"
      responseLength          = "$context.responseLength"
      integrationErrorMessage = "$context.integrationErrorMessage"
    }
    )
  }
}

resource "aws_apigatewayv2_integration" "lambda_integration" {
  api_id = aws_apigatewayv2_api.lambda.id

  integration_uri    = aws_lambda_function.lambda.invoke_arn
  integration_type   = "AWS_PROXY"
  integration_method = "POST"
}

resource "aws_apigatewayv2_route" "lambda_route" {
  api_id = aws_apigatewayv2_api.lambda.id

  route_key = "POST /{version}"
  target    = "integrations/${aws_apigatewayv2_integration.lambda_integration.id}"
}

resource "aws_cloudwatch_log_group" "api_gw" {
  name = "/aws/api_gw/${aws_apigatewayv2_api.lambda.name}"

  retention_in_days = 1
}

resource "aws_lambda_permission" "api_gw" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.lambda.function_name
  principal     = "apigateway.amazonaws.com"

  source_arn = "${aws_apigatewayv2_api.lambda.execution_arn}/*/*"
}


output "url" {
  value = aws_apigatewayv2_api.lambda.api_endpoint
}

output "url_with_stage" {
  value = aws_apigatewayv2_api.lambda.target
}