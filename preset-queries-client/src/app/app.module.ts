import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';


import { AppComponent } from './app.component';
import { QueriesComponent } from './queries/queries.component';
import { QueryDetailComponent } from './query-detail/query-detail.component';
import { QueryService} from './query.service';
import { MessagesComponent } from './messages/messages.component';
import { MessageService } from './message.service';
import { DataTableComponent } from './data-table/data-table.component';
import { HeaderComponent } from './header/header.component';


@NgModule({
  declarations: [
    AppComponent,
    QueriesComponent,
    QueryDetailComponent,
    MessagesComponent,
    DataTableComponent,
    HeaderComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [QueryService, MessageService],
  bootstrap: [AppComponent]
})
export class AppModule { }
