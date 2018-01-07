import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { catchError, map, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { of} from 'rxjs/observable/of';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';


import { MessageService} from './message.service';
import {Query, QueryResponse} from './query';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable()
export class QueryService {

  headerSource: string[] = ['--'];
  dataSource: string[][] = [];

  getQueries(): Observable<Query[]> {
    return this.http.get<Query[]>('api/queries')
      .pipe(
        tap(queries => this.log(`fetched queries`)),
        catchError(this.handleError('getQueries', []))
      );
  }

  postQuery (query: Query): void {
    this.messageService.setMainMessage(null);
    this.http.post<QueryResponse>('api/query', query, httpOptions).pipe(
      tap((queryResponse: QueryResponse) => this.log(`query ${query.name} posted, returned jdbcTemplate ${queryResponse.jdbcTemplate}`)),
      catchError(this.handleError<QueryResponse>('postQuery'))
    ).subscribe((queryResponse: QueryResponse) => {
      if (queryResponse) {
        this.dataSource = queryResponse.data;
        this.headerSource = queryResponse.header;
      }
    });
  }


  constructor(private http: HttpClient, private messageService: MessageService) { }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T> (operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.error.message} (${error.error.status}-${error.error.error})`, true);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

  private log(message: string, isError?: boolean) {
    this.messageService.add('QueryService: ' + message);
    if (isError) {
      this.messageService.setMainMessage(message);
    }
  }

}
