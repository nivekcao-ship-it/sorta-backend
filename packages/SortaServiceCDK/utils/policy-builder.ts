import * as iam from 'aws-cdk-lib/aws-iam';
import * as s3 from 'aws-cdk-lib/aws-s3';
import * as dynamodb from 'aws-cdk-lib/aws-dynamodb';

export class PolicyBuilder {
  
  static bedrockAgentPolicy(region: string, account: string, agentId: string, agentAliasId: string): iam.PolicyStatement {
    return new iam.PolicyStatement({
      actions: [
        'bedrock:InvokeAgent',
        'bedrock:InvokeModel'
      ],
      resources: [
        `arn:aws:bedrock:${region}:${account}:agent-alias/${agentId}/${agentAliasId}`,
        `arn:aws:bedrock:${region}::foundation-model/*`
      ]
    });
  }

  static bedrockPolicy(region: string, account: string): iam.PolicyStatement {
    return new iam.PolicyStatement({
      actions: [
        'bedrock:InvokeModel'
      ],
      resources: [
        `arn:aws:bedrock:${region}::foundation-model/*`
      ]
    });
  }

  static s3BucketPolicy(bucket: s3.Bucket, permissions: string[]): iam.PolicyStatement {
    return new iam.PolicyStatement({
      actions: permissions,
      resources: [
        bucket.bucketArn,
        `${bucket.bucketArn}/*`
      ]
    });
  }

  static s3ReadOnlyPolicy(bucket: s3.Bucket): iam.PolicyStatement {
    return this.s3BucketPolicy(bucket, ['s3:GetObject', 's3:HeadObject', 's3:ListBucket']);
  }

  static s3ReadWritePolicy(bucket: s3.Bucket): iam.PolicyStatement {
    return this.s3BucketPolicy(bucket, ['s3:GetObject', 's3:PutObject', 's3:HeadObject', 's3:ListBucket']);
  }

  static s3FullAccessPolicy(bucket: s3.Bucket): iam.PolicyStatement {
    return this.s3BucketPolicy(bucket, ['s3:GetObject', 's3:PutObject', 's3:DeleteObject', 's3:HeadObject', 's3:ListBucket']);
  }

  static dynamoDbReadWritePolicy(table: dynamodb.Table): iam.PolicyStatement {
    return new iam.PolicyStatement({
      actions: [
        'dynamodb:GetItem',
        'dynamodb:BatchGetItem',
        'dynamodb:PutItem',
        'dynamodb:UpdateItem',
        'dynamodb:DeleteItem',
        'dynamodb:Query',
        'dynamodb:Scan'
      ],
      resources: [table.tableArn]
    });
  }
}