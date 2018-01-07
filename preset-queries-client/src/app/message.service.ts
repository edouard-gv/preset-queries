import { Injectable } from '@angular/core';

@Injectable()
export class MessageService {
  messages: string[] = [];
  mainMessage: string;

  add(message: string) {
    this.messages.push(message);
  }

  setMainMessage(message: string) {
    this.mainMessage = message;
  }

  clear() {
    this.messages = [];
  }
}
