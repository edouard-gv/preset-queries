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
import { LoginComponent } from './login/login.component';
import { RoleService } from "./role.service";
import { QueryUpdateComponent } from './query-update/query-update.component';


@NgModule({
  declarations: [
    AppComponent,
    QueriesComponent,
    QueryDetailComponent,
    MessagesComponent,
    DataTableComponent,
    HeaderComponent,
    LoginComponent,
    QueryUpdateComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [QueryService, MessageService, RoleService],
  bootstrap: [AppComponent]
})
export class AppModule { }
