export const TaskStatus = {
  PENDING: 'PENDING',
  RUNNING: 'RUNNING',
  SUCCESS: 'SUCCESS',
  FAILED: 'FAILED'
};

export const TaskType = {
  SHELL: 'SHELL',
  JAR: 'JAR',
  PYTHON: 'PYTHON',
  SPARK: 'SPARK',
  HTTP: 'HTTP'
};

export const AlertType = {
  EMAIL: 'EMAIL',
  SMS: 'SMS',
  WEBHOOK: 'WEBHOOK'
};

export const ResourceType = {
  TASK: 'TASK',
  DAG: 'DAG',
  USER: 'USER',
  ALERT: 'ALERT'
};

export const ActionType = {
  READ: 'READ',
  WRITE: 'WRITE',
  EXECUTE: 'EXECUTE',
  DELETE: 'DELETE'
};
