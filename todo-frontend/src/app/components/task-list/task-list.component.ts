import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { TaskService } from '../../services/task.service';
import { AuthService } from '../../services/auth.service';
import { Task } from '../../models/task';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.css']
})
export class TaskListComponent implements OnInit {
  tasks: Task[] = [];
  isLoading = true;
  errorMessage = '';

  constructor(
    private taskService: TaskService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    console.log('📋 TaskListComponent initialized');
    this.loadTasks();
  }

  getUserIdFromToken(): number | undefined {
    const token = this.authService.getAccessToken();
    if (!token) return undefined;
    
    try {
      const payload = token.split('.')[1];
      const decoded = JSON.parse(atob(payload));
      console.log('🔍 Decoded token:', decoded);
      return decoded.userId;
    } catch (e) {
      console.error('Failed to decode token', e);
      return undefined;
    }
  }

  loadTasks(): void {
    let userId = this.authService.getUser()?.id;
    
    if (!userId) {
      userId = this.getUserIdFromToken();
      console.log('📡 Using userId from token:', userId);
    }
    
    if (!userId) {
      console.warn('⚠️ No user ID found');
      this.isLoading = false;
      return;
    }

    console.log(`📡 Loading tasks for userId=${userId}`);
    this.isLoading = true;
    
    this.taskService.getTasks(userId).subscribe({
      next: (tasks) => {
        console.log(`✅ Tasks loaded: ${tasks.length} tasks`, tasks);
        this.tasks = tasks;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('❌ Failed to load tasks:', error);
        this.errorMessage = 'Ошибка загрузки задач';
        this.isLoading = false;
      }
    });
  }

  onDeleteTask(taskId: number): void {
    this.taskService.deleteTask(taskId).subscribe({
      next: () => {
        this.tasks = this.tasks.filter(t => t.id !== taskId);
      },
      error: (error) => {
        if (error.status === 403) {
          alert('У вас нет прав на удаление задач');
        } else if (error.status === 400) {
          alert('Нельзя удалить задачу, созданную менее 5 минут назад');
        } else {
          alert('Ошибка при удалении задачи');
        }
      }
    });
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  getStatusName(status: string): string {
    switch (status) {
      case 'OPEN': return '🟢 Открыта';
      case 'IN_PROGRESS': return '🟠 В работе';
      case 'DONE': return '🔵 Выполнена';
      case 'CLOSED': return '⚫ Закрыта';
      default: return status;
    }
  }
}