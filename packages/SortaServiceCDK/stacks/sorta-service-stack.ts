import * as cdk from 'aws-cdk-lib';
import * as path from 'path';
import * as s3 from 'aws-cdk-lib/aws-s3';
import * as s3n from 'aws-cdk-lib/aws-s3-notifications';
import * as dynamodb from 'aws-cdk-lib/aws-dynamodb';
import { Construct } from 'constructs';
import { SortaLambda } from '../constructs/sorta-lambda';
import { SortaS3Bucket } from '../constructs/sorta-s3-bucket';
import { SortaDynamoTable } from '../constructs/sorta-dynamodb-table';
import { SortaBedrockAgent } from '../constructs/sorta-bedrock-agent';
import { PolicyBuilder } from '../utils/policy-builder';
import { SORTA_AGENT_SYSTEM_PROMPT } from '../constants/prompts';
import {SortaApi} from "../constructs/sorta-api";

export class SortaServiceStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // Create S3 buckets
    const imagesBucket = new SortaS3Bucket(this, 'SortaImagesBucket', {
      bucketName: 'sorta-images',
      corsEnabled: true,
      retainOnDelete: true
    });

    // Create DynamoDB tables
    const usersTable = new SortaDynamoTable(this, 'UsersTable', {
      tableName: 'users',
      partitionKey: { name: 'userId', type: dynamodb.AttributeType.STRING },
      retainOnDelete: true
    });

    const roomsTable = new SortaDynamoTable(this, 'RoomsTable', {
      tableName: 'rooms',
      partitionKey: { name: 'userId', type: dynamodb.AttributeType.STRING },
      sortKey: { name: 'spaceId', type: dynamodb.AttributeType.STRING },
      retainOnDelete: true
    });

    const sessionsTable = new SortaDynamoTable(this, 'SessionsTable', {
      tableName: 'sessions',
      partitionKey: { name: 'sessionId', type: dynamodb.AttributeType.STRING },
      sortKey: { name: 'userId', type: dynamodb.AttributeType.STRING },
      enableStreams: true,
      retainOnDelete: true
    });

    // Create action group Lambda
    const actionGroupLambda = new SortaLambda(this, 'ActionGroupLambda', {
      functionName: 'sorta-action-group-handler',
      handler: 'com.sorta.service.handlers.SortaAgentActionGroupHandler::handleRequest',
      codePath: path.join(__dirname, '../../SortaService/app/build/libs/lambda.jar'),
      timeout: cdk.Duration.seconds(30),
      environment: {
        DECLUTTER_IMAGES_BUCKET_NAME: imagesBucket.bucket.bucketName,
        USERS_TABLE_NAME: usersTable.table.tableName,
        ROOMS_TABLE_NAME: roomsTable.table.tableName,
        SESSIONS_TABLE_NAME: sessionsTable.table.tableName,
      },
      policies: [
        PolicyBuilder.s3ReadWritePolicy(imagesBucket.bucket),
        PolicyBuilder.dynamoDbReadWritePolicy(usersTable.table),
        PolicyBuilder.dynamoDbReadWritePolicy(roomsTable.table),
        PolicyBuilder.dynamoDbReadWritePolicy(sessionsTable.table)
      ]
    });

    // Create Bedrock agent
    const bedrockAgent = new SortaBedrockAgent(this, 'SortaBedrockAgent', {
      agentName: 'SortaAgent',
      description: 'The agent for home decluttering',
      instruction: SORTA_AGENT_SYSTEM_PROMPT,
      actionGroupLambda: actionGroupLambda.function,
      apiSchemaPath: path.join(__dirname, '../schema/sorta-core-action-group-api-schema.json')
    });

    // Create API Lambda function
    const sortaApiLambda = new SortaLambda(this, 'ApiLambda', {
      functionName: 'sorta-api-lambda',
      handler: 'com.sorta.service.handlers.SortaServiceHandler::handleRequest',
      codePath: path.join(__dirname, '../../SortaService/app/build/libs/lambda.jar'),
      timeout: cdk.Duration.seconds(30),
      memorySize: 1024,
      environment: {
        BEDROCK_AGENT_ID: bedrockAgent.agentId,
        BEDROCK_AGENT_ALIAS_ID: bedrockAgent.agentAliasId,
        DECLUTTER_IMAGES_BUCKET_NAME: imagesBucket.bucket.bucketName,
        USERS_TABLE_NAME: usersTable.table.tableName,
        ROOMS_TABLE_NAME: roomsTable.table.tableName,
        SESSIONS_TABLE_NAME: sessionsTable.table.tableName,
      },
      policies: [
        PolicyBuilder.bedrockAgentPolicy(this.region, this.account, bedrockAgent.agentId, bedrockAgent.agentAliasId),
        PolicyBuilder.s3ReadWritePolicy(imagesBucket.bucket),
        PolicyBuilder.dynamoDbReadWritePolicy(usersTable.table),
        PolicyBuilder.dynamoDbReadWritePolicy(roomsTable.table),
        PolicyBuilder.dynamoDbReadWritePolicy(sessionsTable.table)
      ]
    });

    // Create S3 Event Handler Lambda function
    const imageEventLambda = new SortaLambda(this, 'ImageEventLambda', {
      functionName: 'sorta-image-event-lambda',
      handler: 'com.sorta.service.handlers.ImageEventHandler::handleRequest',
      codePath: path.join(__dirname, '../../SortaService/app/build/libs/lambda.jar'),
      timeout: cdk.Duration.minutes(5),
      memorySize: 512,
      environment: {
        DECLUTTER_IMAGES_BUCKET_NAME: imagesBucket.bucket.bucketName,
      },
      policies: [
        PolicyBuilder.s3ReadOnlyPolicy(imagesBucket.bucket),
      ]
    });

    // Create API Gateway with endpoints
    const sortaApi = new SortaApi(this, 'SortaApi', {
      apiName: 'Sorta Service API',
      description: 'API for Sorta decluttering service',
      corsEnabled: true,
      endpoints: [
        {
          path: 'v1/agent/conversation',
          method: 'POST',
          handler: sortaApiLambda.function
        },
        {
          path: 'v1/images/presigned-upload-url',
          method: 'POST',
          handler: sortaApiLambda.function
        },
        {
          path: 'v1/users/profile',
          method: 'GET',
          handler: sortaApiLambda.function
        }
      ]
    });

    // Configure S3 event triggers
    imagesBucket.bucket.addEventNotification(
        s3.EventType.OBJECT_CREATED,
        new s3n.LambdaDestination(imageEventLambda.function)
    );
  }
}
