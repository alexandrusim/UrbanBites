import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeedbackService } from '../../core/services/feedback.service';
import { Feedback } from '../../shared/models/feedback.model';

@Component({
  selector: 'app-feedback-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './feedback-list.component.html',
  styleUrls: ['./feedback-list.component.css']
})
export class FeedbackListComponent implements OnInit {
  @Input() restaurantId!: number;
  feedbacks: Feedback[] = [];
  loading: boolean = true;
  errorMessage: string = '';

  constructor(private feedbackService: FeedbackService) {}

  ngOnInit(): void {
    if (this.restaurantId) {
      this.loadFeedbacks();
    }
  }

  loadFeedbacks(): void {
    this.loading = true;
    this.feedbackService.getFeedbackByRestaurant(this.restaurantId).subscribe({
      next: (data) => {
        this.feedbacks = data;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Nu s-au putut încărca recenziile.';
        this.loading = false;
        console.error(err);
      }
    });
  }

  getStars(rating: number): number[] {
    return Array(rating).fill(0);
  }
}