import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TaskService } from '../../services/task.service';
import { TaskStatus, TaskDto } from '../../models/task';

@Component({
  selector: 'app-edit-task',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './edit-task.component.html',
  styleUrls: ['./edit-task.component.css']
})
export class EditTaskComponent implements OnInit {
  taskId: number | null = null;
  isEditMode = false;
  
  taskData: TaskDto = {
    title: '',
    status: TaskStatus.OPEN
  };
  
  statuses = Object.values(TaskStatus);
  isLoading = false;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private taskService: TaskService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id && id !== 'new') {
      this.taskId = +id;
      this.isEditMode = true;
      this.loadTask();
    }
  }

  loadTask(): void {
    if (!this.taskId) return;
    
    this.isLoading = true;
    this.taskService.getTaskById(this.taskId).subscribe({
      next: (task) => {
        this.taskData = { title: task.title, status: task.status };
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Задача не найдена';
        this.isLoading = false;
      }
    });
  }

  onSubmit(): void {
    if (!this.taskData.title.trim()) {
      this.errorMessage = 'Название задачи обязательно';
      return;
    }

    this.isLoading = true;
    
    if (this.isEditMode && this.taskId) {
      this.taskService.updateTask(this.taskId, this.taskData).subscribe({
        next: () => {
          // Передаём параметр refresh=true, чтобы обновить список
          this.router.navigate(['/tasks'], { queryParams: { refresh: Date.now() } });
        },
        error: () => {
          this.errorMessage = 'Ошибка при обновлении задачи';
          this.isLoading = false;
        }
      });
    } else {
      this.taskService.createTask(this.taskData).subscribe({
        next: () => {
          // Передаём параметр refresh=true, чтобы обновить список
          this.router.navigate(['/tasks'], { queryParams: { refresh: Date.now() } });
        },
        error: () => {
          this.errorMessage = 'Ошибка при создании задачи';
          this.isLoading = false;
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/tasks']);
  }
}