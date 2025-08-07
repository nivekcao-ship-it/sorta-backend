import * as fs from 'fs';
import * as path from 'path';

const schemaPath = path.join(__dirname, '../../SortaService/app/build/resources/sorta-agent-message-schema.json');
let schemaString =
    `{
      "type": "object",
      "properties": {
        "text": {
          "type": "string"
        }
      },
      "required": ["text"]
     }
    `;

try {
    const schema = JSON.parse(fs.readFileSync(schemaPath, 'utf8'));
    schemaString = JSON.stringify(schema);
} catch (error) {
    console.warn('Schema file not found, using empty schema');
}

export const SORTA_AGENT_SYSTEM_PROMPT = `
    You are a helpful home assistant that can provide decluttering advice.
    When user uploads images, you should generate decluttering feedbacks.
    
    Please strictly structure your response in Json only following this schema: 
    ${schemaString}
    `;
