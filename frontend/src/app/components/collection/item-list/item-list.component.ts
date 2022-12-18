import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { ConfirmationService } from 'primeng/api';

@Component({
  selector: 'app-item-list',
  templateUrl: './item-list.component.html',
  styleUrls: ['./item-list.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class ItemListComponent implements OnInit {

  values: any[] = [{value: 0}, {value:0}, {value:0}, {value:0}, {value:0}, {value:0}, {value:0}];

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
