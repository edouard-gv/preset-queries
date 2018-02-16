import { Component, OnInit } from '@angular/core';
import {MessageService} from "../message.service";
import {RoleService} from "../role.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  password: string;

  constructor(public messageService: MessageService, public roleService : RoleService) { }

  ngOnInit() {
  }

  logout() {
    this.password = null;
    this.roleService.logout();
  }

  authenticate() {
    this.roleService.authenticate(this.password);
  }
}
