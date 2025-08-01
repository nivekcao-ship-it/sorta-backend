import * as cdk from 'aws-cdk-lib';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as iam from 'aws-cdk-lib/aws-iam';
import { Construct } from 'constructs';

export interface SortaLambdaProps {
  functionName: string;
  handler: string;
  codePath: string;
  environment?: Record<string, string>;
  timeout?: cdk.Duration;
  memorySize?: number;
  policies?: iam.PolicyStatement[];
}

export class SortaLambda extends Construct {
  public readonly function: lambda.Function;

  constructor(scope: Construct, id: string, props: SortaLambdaProps) {
    super(scope, id);

    // Create Lambda function
    this.function = new lambda.Function(this, 'LambdaFunction', {
      // functionName: props.functionName,
      runtime: lambda.Runtime.JAVA_21,
      handler: props.handler,
      code: lambda.Code.fromAsset(props.codePath),
      timeout: props.timeout || cdk.Duration.seconds(30),
      memorySize: props.memorySize || 512,
      environment: props.environment || {}
    });

    // Add policies if specified
    if (props.policies) {
      props.policies.forEach(policy => {
        this.function.addToRolePolicy(policy);
      });
    }
  }

  public grantInvoke(principal: iam.IPrincipal): void {
    this.function.grantInvoke(principal);
  }
}