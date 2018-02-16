import { Injectable } from '@angular/core';
import {MessageService} from "./message.service";
import {Cookie} from "ng2-cookies";
import {HttpClient} from "@angular/common/http";
import { catchError, tap } from 'rxjs/operators';
import {Observable} from "rxjs/Observable";
import { of } from 'rxjs/observable/of';


//the object in which the token is sent by the back
class TokenWrapper {
  token:string;
}

@Injectable()
export class RoleService {
  login: string;
  token: string;

  constructor(private http: HttpClient, private messageService: MessageService) {}

  logout() {
    this.token = null;
    this.login = null;

    Cookie.deleteAll();

    this.messageService.clear();
  }

  authenticate(password: string) {
    if (password == null || this.login == null) {
      this.messageService.setMainMessage("Login or Password are mandatory");
      return;
    }

    this.messageService.add("Trying to log in with "+ this.login + "/" + "***");

    this.http.post<TokenWrapper>('api/login', '{"login":"'+this.login+'", "password":"'+password+'"}' , {headers: {'Content-Type': 'application/json'}})
      .pipe(
        tap(results => this.log(`authentication requested, token found:`+results.token)),
        catchError(this.handleError('Authenticate', []))
      ).subscribe( (result:TokenWrapper) =>
    {
      this.token = result.token

      if (this.token != null && this.token.length > 0) {
        Cookie.set("pq-login", this.login);
        Cookie.set("pq-token", this.token);
      }

    });
  }

  loadData() {
    this.login = Cookie.get("pq-login");
    this.token =  Cookie.get("pq-token");
  }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  protected handleError<T> (operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.error.message} (${error.error.status}-${error.error.error})`, true);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

  protected log(message: string, isError?: boolean) {
    this.messageService.add('RoleService - ' + message);
    if (isError) {
      this.messageService.setMainMessage(message);
    }
  }
}
