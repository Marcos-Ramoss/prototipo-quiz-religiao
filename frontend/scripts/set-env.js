const fs = require('fs');
const path = require('path');

const targetPath = path.resolve(__dirname, '../src/environments/environment.prod.ts');
const rawApiUrl = process.env.API_URL || 'https://prototipo-quiz-religiao.onrender.com/api/quiz';

// Garante formatação correta da URL mesmo se o usuário esquecer o '/api/quiz'
let formattedApiUrl = rawApiUrl.trim().replace(/\/+$/, '');
if (!formattedApiUrl.endsWith('/api/quiz')) {
  if (formattedApiUrl.endsWith('/api')) {
    formattedApiUrl = `${formattedApiUrl}/quiz`;
  } else {
    formattedApiUrl = `${formattedApiUrl}/api/quiz`;
  }
}

const envConfigFile = `// Arquivo gerado dinamicamente durante o build via scripts/set-env.js
export const environment = {
  production: true,
  apiUrl: '${formattedApiUrl}'
};
`;

fs.writeFileSync(targetPath, envConfigFile, { encoding: 'utf8' });
console.log(`[set-env] environment.prod.ts gerado com sucesso com UTF-8!`);
console.log(`[set-env] apiUrl configurada: ${formattedApiUrl}`);

