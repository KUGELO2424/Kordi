import { Component, OnInit } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { StateService } from 'app/services/state.service';

@Component({
  selector: 'app-collection-data',
  templateUrl: './collection-data.component.html',
  styleUrls: ['./collection-data.component.css']
})
export class CollectionDataComponent implements OnInit {

  form: UntypedFormGroup = new UntypedFormGroup({
    title: new UntypedFormControl('', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]),
    description: new UntypedFormControl('', [Validators.required, Validators.minLength(3), Validators.maxLength(500)]),
    endDate: new UntypedFormControl(''),
  });

  images: any[] = [];

  descriptionMaxLength: number = 500;

  state: any;

  currentDate = new Date(); 

  constructor(private stateService: StateService) { }

  ngOnInit(): void {
    this.state = this.stateService.state$.getValue() || {}
    if (Object.keys(this.state).length === 0) {
      return;
    }
    if ("info" in this.state) {
      this.form.controls['title'].setValue(this.state.info.title);
      this.form.controls['description'].setValue(this.state.info.description);
      this.form.controls['endDate'].setValue(this.state.info.endDate);
      if (this.state.info.image !== undefined) {
        this.images.push(this.state.info.image); 
      }
    }
    
  }

  saveState() {
    this.state.info = {};
    this.state.info.title = this.form.value.title;
    this.state.info.description = this.form.value.description;
    this.state.info.endDate = this.form.value.endDate;
    this.state.info.image = this.images[0];
    this.stateService.state$.next(this.state);
  }

  checkValidation(input: string){
    const validation = this.form.get(input)?.invalid && (this.form.get(input)?.dirty || this.form.get(input)?.touched)
    return validation;
  }

  onRemove(event: any) {
    this.images = [];
  }

  onSelect(event: any) {
    this.images = [];
    for(let file of event.files) {
        this.images.push(file);
    }
  }

}
