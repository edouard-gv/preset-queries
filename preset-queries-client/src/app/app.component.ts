import { Component } from '@angular/core';

import {RoleService} from "./role.service";
import {MessageService} from "./message.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Preset Queries';
  constructor(public roleService: RoleService, public messageService: MessageService) { }

  ngOnInit() {
    this.roleService.loadData();
  }
}
