import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AuthRequest } from '../../models/task';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  credentials: AuthRequest = { username: '', password: '' };
  errorMessage = '';
  isLoading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    this.isLoading = true;
    this.errorMessage = '';

    console.log('🔐 Login attempt:', this.credentials.username);

    this.authService.login(this.credentials).subscribe({
      next: (response) => {
        console.log('✅ Login success, tokens received');
        this.authService.saveTokens(response.accessToken, response.refreshToken);
        
        this.authService.getCurrentUser().subscribe({
          next: (user) => {
            console.log('✅ User data received from server:', user);
            // ВАЖНО: user должен содержать id
            if (!user.id) {
              console.error('❌ User ID is missing!', user);
            }
            this.authService.saveUser(user);
            
            // Проверка что сохранилось
            const savedUser = this.authService.getUser();
            console.log('📦 Saved user:', savedUser);
            
            this.router.navigate(['/tasks']);
          },
          error: (err) => {
            console.error('❌ Failed to get user:', err);
            this.errorMessage = 'Ошибка при получении данных пользователя';
            this.isLoading = false;
          }
        });
      },
      error: (error) => {
        console.error('❌ Login failed:', error);
        if (error.status === 401) {
          this.errorMessage = 'Неверное имя пользователя или пароль';
        } else {
          this.errorMessage = 'Ошибка сервера. Попробуйте позже.';
        }
        this.isLoading = false;
      }
    });
  }
}