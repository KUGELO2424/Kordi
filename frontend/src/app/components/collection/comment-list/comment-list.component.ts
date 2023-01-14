import { Component, Input, OnInit } from '@angular/core';
import { CollectionService } from 'app/services/collection.service';
import { Comment } from 'app/common/comment';
import { TranslateService } from '@ngx-translate/core';
import { ViewportScroller } from '@angular/common';

@Component({
  selector: 'app-comment-list',
  templateUrl: './comment-list.component.html',
  styleUrls: ['./comment-list.component.css']
})
export class CommentListComponent implements OnInit {

  @Input() collectionId: string;
  comments: Comment[];

  commentContent: string = "";

  page: number = 0;
  pageSize: number = 10;
  totalRecords: number = 10;

  constructor(private collectionService: CollectionService, 
    private scroller: ViewportScroller) { }

  ngOnInit(): void {
    this.searchComments();
  }

  searchComments() {
    this.collectionService.getCommentsFromCollection(this.collectionId, this.page, this.pageSize).subscribe({
      next: (data) => {   
        this.comments = data.content;
        this.page = data.size;
        this.totalRecords = data.totalElements;
      },
      error: (error) => {
        console.log(error);
      }
    })
  }

  addComment() {
    console.log("ADD COMMENT");
  }

  changePage(event: any) {
    // Scroll if page has been changed
    if (event.rows == this.pageSize && event.page != this.page) {
      this.scroll();
    }
    this.pageSize = event.rows;
    this.page = event.page;
    this.searchComments();
  }

  scroll() {
    this.scroller.scrollToAnchor("commentList");
  }

  isCommentContentInvalid() {
    if (this.commentContent.length > 250) {
      return true;
    }
    return false;
  }

}
