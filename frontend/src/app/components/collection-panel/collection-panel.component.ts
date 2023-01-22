import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Collection } from 'app/common/collection';
import { SubmittedItem } from 'app/common/submittedItem';
import { CollectionService } from 'app/services/collection.service';
import { Location } from '@angular/common'
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { UpdateCollection } from 'app/common/updateCollection';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-collection-panel',
  templateUrl: './collection-panel.component.html',
  styleUrls: ['./collection-panel.component.css']
})
export class CollectionPanelComponent implements OnInit {

  collection: Collection | undefined;
  submittedItems: SubmittedItem[] = [];
  collectionProgress: number = 0;
  currentDate = new Date(); 
  
  form: UntypedFormGroup = new UntypedFormGroup({
    title: new UntypedFormControl('', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]),
    description: new UntypedFormControl('', [Validators.required, Validators.minLength(3), Validators.maxLength(500)]),
    endDate: new UntypedFormControl(''),
  });

  constructor(private route: ActivatedRoute, private collectionService: CollectionService, private location: Location,
    private messageService: MessageService) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(() => {
      this.handleCollectionDetails();
    })
  }

  handleCollectionDetails() {
    const collectionId: string = this.route.snapshot.paramMap.get('id')!;
    this.collectionService.getCollectionById(collectionId).subscribe({
      next: (data) => {   
        this.collection = data;
        this.form.controls['title'].setValue(this.collection.title);
        this.form.controls['description'].setValue(this.collection.description);
        this.form.controls['endDate'].setValue(this.collection.endTime);
        this.handleSubmittedItemsDetails(collectionId)
        this.setCollectionProgress();
      },
      error: () => {

      }
    })
  }

  handleSubmittedItemsDetails(id: string) {
    this.collectionService.getSubmittedItemsFromCollection(id).subscribe({
      next: (data) => {
        this.submittedItems = data;
      }
    })
  }

  submit() {
    let collectionUpdate = new UpdateCollection();
    collectionUpdate.id = this.collection!.id
    collectionUpdate.title = this.form.value.title;
    collectionUpdate.description = this.form.value.description;
    collectionUpdate.endTime = this.form.value.endDate;
    this.collectionService.updateCollection(collectionUpdate).subscribe({
      next: (data) => {
        this.messageService.add({severity:'success', detail:'Via MessageService'});
      },
      error: (error) => {
        console.log(error);
      }
    })
  }

  setCollectionProgress() {
    this.collectionService.getCollectionProgress(this.collection!.items).subscribe({
      next: (data) => {
        this.collectionProgress = data;
      }
    })
  }

  back() {
    this.location.back();
  }

  checkValidation(input: string){
    const validation = this.form.get(input)?.invalid && (this.form.get(input)?.dirty || this.form.get(input)?.touched)
    return validation;
  }

}
