// Owner-run read-only setup check. Credentials stay in memory and are never printed.
import { spawnSync } from 'node:child_process';
import path from 'node:path';
import fs from 'node:fs';
import { fileURLToPath } from 'node:url';

const worker = 'ash-dev91';
const repository = 'Mohad009/Riyal_App';
const revision = '5e4e75d31f7e51bc66904b538ef33a30ca0c0ad7';
const root = path.dirname(path.dirname(fileURLToPath(import.meta.url)));
const gh = path.join(process.env.ProgramFiles ?? 'C:\\Program Files', 'GitHub CLI', 'gh.exe');
const env = { ...process.env, GH_HOST: 'github.com', GH_PROMPT_DISABLED: '1' };
for (const key of ['GH_TOKEN', 'GITHUB_TOKEN', 'GH_ENTERPRISE_TOKEN', 'GITHUB_ENTERPRISE_TOKEN']) delete env[key];
delete env.WF_WORKER_TOKEN;
for (const key of Object.keys(env)) if (key.startsWith('GIT_TRACE') || key === 'GH_DEBUG') delete env[key];

function call(args, failure) {
  const result = spawnSync(gh, args, { encoding: 'utf8', env, timeout: 30000, maxBuffer: 1024 * 1024 });
  if (result.error || result.status !== 0) throw new Error(failure);
  return result.stdout.trim();
}

try {
  if (process.argv.slice(2).some(arg => arg !== '--git') || process.argv.length > 3) throw new Error('Usage: node scripts/check-worker-access.mjs [--git]');
  if (!process.env.LOCALAPPDATA) throw new Error('Windows local application directory is unavailable.');
  env.GH_CONFIG_DIR = path.join(process.env.LOCALAPPDATA, 'Riyal-Agent', 'gh');
  const version = call(['--version'], 'GitHub CLI is unavailable.').split(/\r?\n/)[0];
  if (!version.startsWith('gh version 2.101.0 ')) throw new Error('GitHub CLI version changed; review the new version before continuing.');
  const token = call(['auth', 'token', '--hostname', 'github.com', '--user', worker], 'The named worker credential could not be loaded; no other account was tried.');
  if (!token || /\s/.test(token)) throw new Error('The named worker credential is missing or invalid.');
  env.GH_TOKEN = token;
  const login = call(['api', 'user', '--jq', '.login'], 'The worker identity could not be verified.');
  if (login !== worker) throw new Error('The authenticated account is not ash-dev91; check stopped.');
  const details = JSON.parse(call([
    'api', `repos/${repository}`, '--jq',
    '{repository: .full_name, owner: .owner.login, owner_type: .owner.type, can_push: .permissions.push, can_admin: .permissions.admin}'
  ], 'The repository could not be read using the worker credential.'));
  if (details.repository !== repository || details.owner !== 'Mohad009' || details.owner_type !== 'User') {
    throw new Error('The repository does not match the expected personal owner.');
  }
  console.log(`Account: ${login}`);
  console.log(`Repository: ${details.repository}`);
  console.log(`Push permission: ${details.can_push === true}`);
  console.log(`Admin permission: ${details.can_admin === true}`);
  if (details.can_push !== true || details.can_admin !== false) throw new Error('Expected collaborator permissions were not confirmed.');
  if (process.argv.includes('--git')) {
    const cache = path.join(root, '.cache', 'agent-workflow');
    // Check the exact supplied adapter source against the immutable Git objects.
    // This remains local setup feedback, not an authoritative integration gate.
    for (const file of ['adapters/identity.mjs', 'adapters/worker-askpass.sh']) {
      const committed = spawnSync('git', ['-C', cache, 'show', `${revision}:${file}`], {
        encoding: 'utf8', env, timeout: 10000, maxBuffer: 1024 * 1024
      });
      if (committed.error || committed.status !== 0) throw new Error('The pinned workflow adapter could not be read.');
      let installed;
      try { installed = fs.readFileSync(path.join(cache, file), 'utf8'); }
      catch { throw new Error('The installed workflow adapter is missing.'); }
      if (installed.replace(/\r\n/g, '\n') !== committed.stdout.replace(/\r\n/g, '\n')) {
        throw new Error('The installed workflow adapter differs from the pinned source; check stopped.');
      }
    }
    const adapterEnv = {
      ...env,
      WF_WORKER_TOKEN: token,
      PATH: `${path.dirname(gh)}${path.delimiter}${process.env.PATH ?? process.env.Path ?? ''}`
    };
    const result = spawnSync(process.execPath, [
      path.join(cache, 'adapters', 'identity.mjs'), repository, worker, 'master'
    ], { cwd: root, env: adapterEnv, encoding: 'utf8', timeout: 120000, maxBuffer: 1024 * 1024 });
    delete adapterEnv.WF_WORKER_TOKEN;
    delete adapterEnv.GH_TOKEN;
    let report;
    try { report = JSON.parse(result.stdout ?? ''); }
    catch { throw new Error('The Git route check could not return a result.'); }
    if (result.error || result.status !== 0 || report.ok !== true) {
      throw new Error('The isolated worker Git route was not verified; no account fallback was attempted.');
    }
    console.log(`Git route: ${report.route}`);
    console.log(`Branch checked: ${report.branch}`);
    console.log('Read-only Git check passed. Actual push and protection tests are still pending.');
  } else {
    console.log('Read-only API check passed. Git transport and protection tests are still pending.');
  }
} catch (error) {
  // Never echo subprocess output or a malformed API response.
  console.error(error instanceof SyntaxError ? 'Repository response could not be read; check stopped.' : error.message);
  process.exitCode = 1;
} finally {
  delete env.GH_TOKEN;
}
