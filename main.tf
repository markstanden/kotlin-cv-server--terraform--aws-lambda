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

resource "aws_iam_role" "lambda-iam" {
  name = "lambda-iam--cv-server"

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
  source_code_hash = base64sha256(filebase64(var.function_jar))
  function_name    = var.name
  role             = aws_iam_role.lambda-iam.arn
  handler          = "dev.markstanden.Application::handleRequest"
  runtime          = "java11"
}

resource "aws_api_gateway_rest_api" "lambda-api" {
  name = "rest-api"
}

resource "aws_api_gateway_resource" "rest-resource" {
  rest_api_id = aws_api_gateway_rest_api.lambda-api.id
  parent_id   = aws_api_gateway_rest_api.lambda-api.root_resource_id
  path_part   = "test"
  lifecycle {}
}

resource "aws_api_gateway_method" "rest-post" {
  authorization = "NONE"
  http_method   = "POST"
  resource_id   = aws_api_gateway_resource.rest-resource.id
  rest_api_id   = aws_api_gateway_rest_api.lambda-api.id
}

#resource "aws_apigatewayv2_stage" "lambda-stage" {
#  api_id      = aws_apigatewayv2_api.lambda-api.id
#  name        = "default"
#  auto_deploy = true
#}
#

resource "aws_api_gateway_integration" "integration" {
  rest_api_id             = aws_api_gateway_rest_api.lambda-api.id
  resource_id             = aws_api_gateway_resource.rest-resource.id
  http_method             = aws_api_gateway_method.rest-post.http_method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.lambda.invoke_arn
}


#resource "aws_apigatewayv2_integration" "lambda-integration" {
#  api_id               = aws_apigatewayv2_api.lambda-api.id
#  integration_type     = "AWS_PROXY"
#  integration_uri      = aws_lambda_function.lambda.invoke_arn
#  passthrough_behavior = "WHEN_NO_MATCH"
#}

#resource "aws_apigatewayv2_route" "lambda_route" {
#  api_id    = aws_apigatewayv2_api.lambda-api.id
#  route_key = "$default"
#  target    = "integrations/${aws_apigatewayv2_integration.lambda-integration.id}"
#}

resource "aws_lambda_permission" "api-gw" {
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.lambda.arn
  principal     = "apigateway.amazonaws.com"
  statement_id  = "AllowExecutionFromAPIGateway"
  source_arn    = "${aws_api_gateway_rest_api.lambda-api.execution_arn}/*/*/*"
}


#resource "aws_lambda_function_url" "lambda-url" {
#  authorization_type = "NONE"
#  function_name      = aws_lambda_function.lambda.arn
#}