import { ChangeDetectionStrategy, ViewContainerRef, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { _HttpClient } from '@delon/theme';
import { format, addDays } from 'date-fns';
import { NzSafeAny } from 'ng-zorro-antd/core/types';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzModalRef, NzModalService } from 'ng-zorro-antd/modal';
import { NzTableQueryParams } from 'ng-zorro-antd/table';

import { AccountsStrategyService } from '../../../service/accounts-strategy.service';
import { set2String } from '../../../shared/index';
import { AccountsStrategyEditerComponent } from './accounts-strategy-editer/accounts-strategy-editer.component';

@Component({
  selector: 'app-accounts-strategy',
  templateUrl: './accounts-strategy.component.html',
  styleUrls: ['./accounts-strategy.component.less']
})
export class AccountsStrategyComponent implements OnInit {
  query: {
    params: {
      name: String;
      displayName: String;
      protocol: String;
      startDate: String;
      endDate: String;
      startDatePicker: Date;
      endDatePicker: Date;
      pageSize: number;
      pageNumber: number;
      pageSizeOptions: number[];
    };
    results: {
      records: number;
      rows: NzSafeAny[];
    };
    expandForm: Boolean;
    submitLoading: boolean;
    tableLoading: boolean;
    tableCheckedId: Set<String>;
    indeterminate: boolean;
    checked: boolean;
  } = {
      params: {
        name: '',
        displayName: '',
        protocol: '',
        startDate: '',
        endDate: '',
        startDatePicker: addDays(new Date(), -30),
        endDatePicker: new Date(),
        pageSize: 10,
        pageNumber: 1,
        pageSizeOptions: [10, 20, 50]
      },
      results: {
        records: 0,
        rows: []
      },
      expandForm: false,
      submitLoading: false,
      tableLoading: false,
      tableCheckedId: new Set<String>(),
      indeterminate: false,
      checked: false
    };

  constructor(
    private modalService: NzModalService,
    private viewContainerRef: ViewContainerRef,
    private accountsStrategyService: AccountsStrategyService,
    private fb: FormBuilder,
    private msg: NzMessageService,
    private cdr: ChangeDetectorRef,
    private http: _HttpClient
  ) { }

  ngOnInit(): void {
    this.fetch();
  }

  onQueryParamsChange(tableQueryParams: NzTableQueryParams): void {
    this.query.params.pageNumber = tableQueryParams.pageIndex;
    this.query.params.pageSize = tableQueryParams.pageSize;
    this.fetch();
  }

  onSearch(): void {
    this.fetch();
  }

  onReset(): void { }

  onBatchDelete(e: MouseEvent): void {
    e.preventDefault();
    this.accountsStrategyService.delete(set2String(this.query.tableCheckedId)).subscribe(res => {
      if (res.code == 0) {
        this.msg.success(`提交成功`);
        this.fetch();
      } else {
        this.msg.success(`提交失败`);
      }
      this.cdr.detectChanges();
    });
  }

  onAdd(e: MouseEvent): void {
    e.preventDefault();
    const modal = this.modalService.create({
      nzContent: AccountsStrategyEditerComponent,
      nzViewContainerRef: this.viewContainerRef,
      nzComponentParams: {
        isEdit: false,
        id: ''
      },
      nzOnOk: () => new Promise(resolve => setTimeout(resolve, 1000))
    });
    // Return a result when closed
    modal.afterClose.subscribe(result => {
      if (result.refresh) {
        this.fetch();
      }
    });
  }

  onEdit(e: MouseEvent, editId: String): void {
    e.preventDefault();

    const modal = this.modalService.create({
      nzContent: AccountsStrategyEditerComponent,
      nzViewContainerRef: this.viewContainerRef,
      nzComponentParams: {
        isEdit: true,
        id: editId
      },
      nzOnOk: () => new Promise(resolve => setTimeout(resolve, 1000))
    });
    // Return a result when closed
    modal.afterClose.subscribe(result => {
      if (result.refresh) {
        this.fetch();
      }
    });
  }

  onDelete(e: MouseEvent, deleteId: String): void {
    e.preventDefault();
    this.accountsStrategyService.delete(deleteId).subscribe(res => {
      if (res.code == 0) {
        this.msg.success(`提交成功`);
        this.fetch();
      } else {
        this.msg.success(`提交失败`);
      }
      this.cdr.detectChanges();
    });
  }

  fetch(): void {
    this.query.submitLoading = true;
    this.query.tableLoading = true;
    this.query.indeterminate = false;
    this.query.checked = false;
    this.query.tableCheckedId.clear();
    if (this.query.expandForm) {
      this.query.params.endDate = format(this.query.params.endDatePicker, 'yyyy-MM-dd HH:mm:ss');
      this.query.params.startDate = format(this.query.params.startDatePicker, 'yyyy-MM-dd HH:mm:ss');
    } else {
      this.query.params.endDate = '';
      this.query.params.startDate = '';
    }
    this.accountsStrategyService.fetch(this.query.params).subscribe(res => {
      this.query.results = res.data;
      this.query.submitLoading = false;
      this.query.tableLoading = false;
      this.cdr.detectChanges();
    });
  }

  updateTableCheckedSet(id: String, checked: boolean): void {
    if (checked) {
      this.query.tableCheckedId.add(id);
    } else {
      this.query.tableCheckedId.delete(id);
    }
  }

  refreshTableCheckedStatus(): void {
    const listOfEnabledData = this.query.results.rows.filter(({ disabled }) => !disabled);
    this.query.checked = listOfEnabledData.every(({ id }) => this.query.tableCheckedId.has(id));
    this.query.indeterminate = listOfEnabledData.some(({ id }) => this.query.tableCheckedId.has(id)) && !this.query.checked;
  }

  onTableItemChecked(id: String, checked: boolean): void {
    this.updateTableCheckedSet(id, checked);
    this.refreshTableCheckedStatus();
  }

  onTableAllChecked(checked: boolean): void {
    this.query.results.rows.filter(({ disabled }) => !disabled).forEach(({ id }) => this.updateTableCheckedSet(id, checked));
    this.refreshTableCheckedStatus();
  }
}
