export interface Task {
  id: number;
  title: string;
  status: TaskStatus;
  createdBy: number;
  createdAt: string;
}

export enum TaskStatus {
  OPEN = 'OPEN',
  IN_PROGRESS = 'IN_PROGRESS',
  DONE = 'DONE',
  CLOSED = 'CLOSED'
}

// Один DTO для создания и обновления (поля одинаковые)
export interface TaskDto {
  title: string;
  status: TaskStatus;
}

export interface User {
  id: number;
  username: string;
  roles: string[];
}

export interface AuthRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
}