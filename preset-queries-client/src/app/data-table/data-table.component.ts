import {Component, Input, OnInit} from '@angular/core';
import {QueryService} from '../query.service';

@Component({
  selector: 'app-data-table',
  templateUrl: './data-table.component.html',
  styleUrls: ['./data-table.component.css']
})
export class DataTableComponent implements OnInit {

  constructor(public queryService: QueryService) { }

  ngOnInit() {
  }

}
