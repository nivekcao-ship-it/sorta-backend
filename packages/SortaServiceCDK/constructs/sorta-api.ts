import * as cdk from 'aws-cdk-lib';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as logs from 'aws-cdk-lib/aws-logs';
import * as iam from 'aws-cdk-lib/aws-iam';
import { Construct } from 'constructs';

export interface ApiEndpoint {
  path: string;
  method: string;
  handler: lambda.Function;
}

export interface SortaApiProps {
  apiName: string;
  description?: string;
  endpoints: ApiEndpoint[];
  corsEnabled?: boolean;
}

export class SortaApi extends Construct {
  public readonly api: apigateway.RestApi;

  constructor(scope: Construct, id: string, props: SortaApiProps) {
    super(scope, id);

    // Add this to your stack (not in the SortaApi construct, but in your main stack):
    const apiGatewayCloudWatchRole = new iam.Role(this, 'ApiGatewayCloudWatchRole', {
      assumedBy: new iam.ServicePrincipal('apigateway.amazonaws.com'),
      managedPolicies: [
        iam.ManagedPolicy.fromAwsManagedPolicyName('service-role/AmazonAPIGatewayPushToCloudWatchLogs')
      ]
    });

    // Set the account-level CloudWatch role for API Gateway
    new apigateway.CfnAccount(this, 'ApiGatewayAccount', {
      cloudWatchRoleArn: apiGatewayCloudWatchRole.roleArn
    });

    // Create API Gateway
    this.api = new apigateway.RestApi(this, 'ApiGateway', {
      restApiName: props.apiName,
      description: props.description || `API for ${props.apiName}`,
      defaultCorsPreflightOptions: props.corsEnabled ? {
        allowOrigins: apigateway.Cors.ALL_ORIGINS,
        allowMethods: apigateway.Cors.ALL_METHODS,
        allowHeaders: ['Content-Type', 'Authorization', 'X-Api-Key'],
        maxAge: cdk.Duration.days(1)
      } : undefined,
      deployOptions: {
        accessLogDestination: new apigateway.LogGroupLogDestination(
            new logs.LogGroup(this, 'AccessLogs', {
              retention: logs.RetentionDays.ONE_MONTH
            })
        ),
        accessLogFormat: apigateway.AccessLogFormat.jsonWithStandardFields()
      },
    });

    // Create endpoints
    this.createEndpoints(props.endpoints);

    // Output API URL
    new cdk.CfnOutput(this, 'ApiUrl', {
      value: this.api.url,
      description: `URL of the ${props.apiName}`
    });
  }

  private createEndpoints(endpoints: ApiEndpoint[]): void {
    endpoints.forEach(endpoint => {
      const pathParts = endpoint.path.split('/').filter(part => part !== '');
      let currentResource = this.api.root;
      
      // Build nested resources
      for (const part of pathParts) {
        const existingResource = currentResource.getResource(part);
        currentResource = existingResource || currentResource.addResource(part);
      }
      
      currentResource.addMethod(endpoint.method, new apigateway.LambdaIntegration(endpoint.handler));
    });
  }
}