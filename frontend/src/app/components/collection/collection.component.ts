import { Component, OnInit } from '@angular/core';
import { ConfirmationService } from 'primeng/api';

@Component({
  selector: 'app-collection',
  templateUrl: './collection.component.html',
  styleUrls: ['./collection.component.css']
})
export class CollectionComponent implements OnInit {

  constructor(private confirmationService: ConfirmationService) { }

  ngOnInit(): void {
  }

  confirm(event: Event) {
    this.confirmationService.confirm({
        target: event.target ? event.target : undefined,
        message: 'Czy na pewno chcesz przejść do podsumowania?',
        icon: 'pi pi-exclamation-triangle',
        accept: () => {
            console.log("AKCEPT")
        },
        reject: () => {
          console.log("CANCLE")
        }
    });
}

}
