import * as cdk from 'aws-cdk-lib';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import * as lambda from 'aws-cdk-lib/aws-lambda';
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

    // Create API Gateway
    this.api = new apigateway.RestApi(this, 'ApiGateway', {
      restApiName: props.apiName,
      description: props.description || `API for ${props.apiName}`,
      defaultCorsPreflightOptions: props.corsEnabled ? {
        allowOrigins: apigateway.Cors.ALL_ORIGINS,
        allowMethods: apigateway.Cors.ALL_METHODS,
        allowHeaders: ['Content-Type', 'Authorization', 'X-Api-Key'],
        maxAge: cdk.Duration.days(1)
      } : undefined
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