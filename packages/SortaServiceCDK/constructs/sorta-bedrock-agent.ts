import * as cdk from 'aws-cdk-lib';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as iam from 'aws-cdk-lib/aws-iam';
import { bedrock } from '@cdklabs/generative-ai-cdk-constructs';
import { Construct } from 'constructs';

export interface SortaBedrockAgentProps {
  agentName: string;
  description: string;
  instruction: string;
  actionGroupLambda: lambda.Function;
  apiSchemaPath: string;
}

export class SortaBedrockAgent extends Construct {
  public readonly agent: bedrock.Agent;
  public readonly agentAlias: bedrock.AgentAlias;
  public readonly agentId: string;
  public readonly agentAliasId: string;

  constructor(scope: Construct, id: string, props: SortaBedrockAgentProps) {
    super(scope, id);

    // Set up agent action group
    const actionGroup = new bedrock.AgentActionGroup({
      name: `${props.agentName}-ActionGroup`,
      description: `${props.description} action group`,
      apiSchema: bedrock.ApiSchema.fromLocalAsset(props.apiSchemaPath),
      enabled: true,
      executor: bedrock.ActionGroupExecutor.fromlambdaFunction(props.actionGroupLambda)
    });

    // Set up agent
    this.agent = new bedrock.Agent(this, 'BedrockAgent', {
      // name: props.agentName,
      description: props.description,
      foundationModel: bedrock.BedrockFoundationModel.ANTHROPIC_CLAUDE_3_5_HAIKU_V1_0,
      instruction: props.instruction,
      idleSessionTTL: cdk.Duration.minutes(10),
      shouldPrepareAgent: true,
      actionGroups: [actionGroup],
    });

    // Grant Bedrock agent permission to invoke the action group Lambda
    props.actionGroupLambda.addPermission('BedrockAgentInvoke', {
      principal: new iam.ServicePrincipal('bedrock.amazonaws.com'),
      action: 'lambda:InvokeFunction',
      sourceArn: `arn:aws:bedrock:${cdk.Stack.of(this).region}:${cdk.Stack.of(this).account}:agent/${this.agent.agentId}`
    });

    // Create agent alias
    this.agentAlias = new bedrock.AgentAlias(this, 'AgentAlias', {
      agent: this.agent,
      aliasName: 'LIVE',
    });

    this.agentId = this.agent.agentId;
    this.agentAliasId = this.agentAlias.aliasId;

    // Outputs
    new cdk.CfnOutput(this, 'AgentId', {
      value: this.agent.agentId,
      description: `ID of the ${props.agentName} agent`
    });

    new cdk.CfnOutput(this, 'AgentAliasId', {
      value: this.agentAlias.aliasId,
      description: `Alias ID of the ${props.agentName} agent`
    });
  }
}