import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { catchError, tap } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { of } from 'rxjs/observable/of';


import { MessageService} from './message.service';
import {Configuration, ParameterType, Query, QueryResponse, StructuredQueryResponse} from './query';
import {RoleService} from './role.service';



@Injectable()
export class QueryService {

  mainQueryResponse: StructuredQueryResponse;

  private buildHttpPostOptions() {
    return {headers: {'Content-Type': 'application/json', 'KoffePot-Token': this.roleService.token}};
  }

  private buildHttpGetOptions() {
    return {headers: {'KoffePot-Token': this.roleService.token}};
  }

  getQueries(): Observable<Query[]> {
    return this.http.get<Query[]>('api/queries', this.buildHttpGetOptions())
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

  getParameterTypes(): Observable<ParameterType[]> {
    return this.http.get<ParameterType[]>('api/parameter-types')
      .pipe(
        tap(results => this.log(`fetched parameter types`)),
        catchError(this.handleError('getParameterTypes', []))
      );
  }

  executeQuery (query: Query): Observable<QueryResponse> {
    this.messageService.setMainMessage(null);
    return this.http.post<QueryResponse>('api/execute', query, this.buildHttpPostOptions()).pipe(
      tap((queryResponse: QueryResponse) => this.log(`query ${query.name} posted, returned jdbcTemplate ${queryResponse.jdbcTemplate}`)),
      catchError(this.handleError<QueryResponse>('executeQuery'))
    );
  }

  executeMainQuery(query: Query): void {
    this.executeQuery(query).subscribe((serviceQueryResponse: QueryResponse) => {
      if (serviceQueryResponse) {
        this.mainQueryResponse.updateFromServiceQueryResponse(serviceQueryResponse);
      }
    });
  }

  updateQuery (query: Query): void {
    this.http.post<Query>('api/query', query, this.buildHttpPostOptions()).pipe(
      tap((queryResponse: Query) => this.log(`query ${query.name} updated`)),
      catchError(this.handleError<Query>('updateQuery', query))
    ).subscribe((result: Query) => {
      (new Query()).merge(result, query);
    });

  }

  reloadQuery (query: Query): void {
    this.http.get<Query>('api/query/' + query.id, this.buildHttpGetOptions())
      .pipe(
        tap(q => this.log(`query ${q.name} reloaded`)),
        catchError(this.handleError('reloadQuery', query))
      ).subscribe(result => {
      (new Query()).merge(result, query);
      });
  }

  constructor(private http: HttpClient, private messageService: MessageService, private roleService: RoleService) {
    this.mainQueryResponse = new StructuredQueryResponse();
  }

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
