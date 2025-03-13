export interface ExecutionRecord {
  id: string;
  dagId: string;
  status: 'RUNNING' | 'SUCCESS' | 'FAILED';
  startTime: string;
  endTime?: string;
  duration?: number;
  error?: string;
  nodes: NodeExecution[];
}

export interface NodeExecution {
  id: string;
  nodeId: string;
  nodeName: string;
  status: 'PENDING' | 'RUNNING' | 'SUCCESS' | 'FAILED';
  startTime?: string;
  endTime?: string;
  error?: string;
}
