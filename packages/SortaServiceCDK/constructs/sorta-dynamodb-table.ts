import * as cdk from 'aws-cdk-lib';
import * as dynamodb from 'aws-cdk-lib/aws-dynamodb';
import { Construct } from 'constructs';

export interface TableKey {
  name: string;
  type: dynamodb.AttributeType;
}

export interface SortaDynamoTableProps {
  tableName: string;
  partitionKey: TableKey;
  sortKey?: TableKey;
  enableStreams?: boolean;
  retainOnDelete?: boolean;
}

export class SortaDynamoTable extends Construct {
  public readonly table: dynamodb.Table;

  constructor(scope: Construct, id: string, props: SortaDynamoTableProps) {
    super(scope, id);

    this.table = new dynamodb.Table(this, 'DynamoDbTable', {
      partitionKey: props.partitionKey,
      sortKey: props.sortKey,
      billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
      encryption: dynamodb.TableEncryption.AWS_MANAGED,
      pointInTimeRecoverySpecification: {
        pointInTimeRecoveryEnabled: true
      },
      removalPolicy: props.retainOnDelete ? cdk.RemovalPolicy.RETAIN : cdk.RemovalPolicy.DESTROY,
      stream: props.enableStreams ? dynamodb.StreamViewType.NEW_AND_OLD_IMAGES : undefined,
    });

    new cdk.CfnOutput(this, 'TableName', {
      value: this.table.tableName,
      description: `Name of the ${props.tableName} table`
    });
  }
}