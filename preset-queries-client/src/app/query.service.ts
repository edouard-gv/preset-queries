import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { catchError, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { of } from 'rxjs/observable/of';


import { MessageService} from './message.service';
import {Configuration, Query, QueryResponse} from './query';

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

  getConfigurations(): Observable<Configuration[]> {
    return this.http.get<Configuration[]>('api/configurations')
      .pipe(
        tap(results => this.log(`fetched configurations`)),
        catchError(this.handleError('getConfigurations', []))
      );
  }

  executeQuery (query: Query): void {
    this.messageService.setMainMessage(null);
    this.http.post<QueryResponse>('api/execute', query, httpOptions).pipe(
      tap((queryResponse: QueryResponse) => this.log(`query ${query.name} posted, returned jdbcTemplate ${queryResponse.jdbcTemplate}`)),
      catchError(this.handleError<QueryResponse>('executeQuery'))
    ).subscribe((queryResponse: QueryResponse) => {
      if (queryResponse) {
        this.dataSource = queryResponse.data;
        this.headerSource = queryResponse.header;
      }
    });
  }

  updateQuery (query: Query): void {
    this.http.post<Query>('api/query', query, httpOptions).pipe(
      tap((queryResponse: Query) => this.log(`query ${query.name} updated`)),
      catchError(this.handleError<Query>('executeQuery', query))
    ).subscribe((result: Query) => {
      (new Query()).merge(result, query);
    });

  }

  editQuery (query: Query): void {
    query.isEdited = true;
  }

  reloadQuery (query: Query): void {
    this.http.get<Query>('api/query/'+query.id)
      .pipe(
        tap(query => this.log(`query ${query.name} reloaded`)),
        catchError(this.handleError('reloadQuery', query))
      ).subscribe(result => {
      (new Query()).merge(result, query);
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
    this.messageService.add('QueryService - ' + message);
    if (isError) {
      this.messageService.setMainMessage(message);
    }
  }

}
