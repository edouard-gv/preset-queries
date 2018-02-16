import { Injectable } from '@angular/core';

@Injectable()
export class MessageService {
  messages: string[] = [];
  mainMessage: string;

  add(message: string) {
    this.messages.push(new Date().toLocaleString() + " - " + message);
  }

  setMainMessage(message: string) {
    this.add(message);
    this.mainMessage = message;
  }

  clear() {
    this.messages = [];
    this.mainMessage = null;
  }
}
