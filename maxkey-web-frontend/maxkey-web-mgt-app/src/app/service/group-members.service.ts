import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NzSafeAny } from 'ng-zorro-antd/core/types';
import { Observable } from 'rxjs';

import { GroupMembers } from '../entity/GroupMembers';
import { Message } from '../entity/Message';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class GroupMembersService extends BaseService<GroupMembers> {
  constructor(private _httpClient: HttpClient) {
    super(_httpClient, '/access/groupmembers');
    this.server.urls.member = '/memberInGroup';
    this.server.urls.memberOut = '/memberNotInGroup';
  }
}
