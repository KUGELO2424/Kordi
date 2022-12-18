import { Component, OnInit } from '@angular/core';
import { LoaderService } from 'app/services/loader.service';

@Component({
  selector: 'app-loading-spinner',
  templateUrl: './loading-spinner.component.html',
  styleUrls: ['./loading-spinner.component.css']
})
export class LoadingSpinnerComponent implements OnInit {

  constructor(public loader: LoaderService) { }

  ngOnInit(): void {
  }

}
