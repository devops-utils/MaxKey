import { Component, ChangeDetectorRef, ViewContainerRef, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { _HttpClient } from '@delon/theme';
import format from 'date-fns/format';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzModalRef, NzModalService } from 'ng-zorro-antd/modal';
import { NzUploadFile, NzUploadChangeParam } from 'ng-zorro-antd/upload';

import { AppsSamlDetails } from '../../../entity/AppsSamlDetails';
import { AppsSamlDetailsService } from '../../../service/apps-saml-details.service';
import { AppsService } from '../../../service/apps.service';
import { SelectAdaptersComponent } from '../../config/adapters/select-adapters/select-adapters.component';

const getBase64 = (file: File): Promise<string | ArrayBuffer | null> =>
  new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result);
    reader.onerror = error => reject(error);
  });
@Component({
  selector: 'app-app-saml20-details-editer',
  templateUrl: './app-saml20-details-editer.component.html',
  styles: [
    `
      nz-tabset {
        width: 90%;
      }

      nz-form-item {
        width: 50%;
      }
    `
  ],
  styleUrls: ['./app-saml20-details-editer.component.less']
})
export class AppSaml20DetailsEditerComponent implements OnInit {
  @Input() id?: String;
  @Input() isEdit?: boolean;

  form: {
    submitting: boolean;
    model: AppsSamlDetails;
  } = {
      submitting: false,
      model: new AppsSamlDetails()
    };

  formGroup: FormGroup = new FormGroup({});

  fileList: NzUploadFile[] = [];

  previewImage: string | ArrayBuffer | undefined | null = '';
  previewVisible = false;

  constructor(
    private modal: NzModalRef,
    private modalService: NzModalService,
    private appsService: AppsService,
    private appsSamlDetailsService: AppsSamlDetailsService,
    private viewContainerRef: ViewContainerRef,
    private fb: FormBuilder,
    private msg: NzMessageService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    if (this.isEdit) {
      this.appsSamlDetailsService.get(`${this.id}`).subscribe(res => {
        this.form.model.init(res.data);
        this.previewImage = this.form.model.iconBase64.toString();
        this.fileList = [
          {
            uid: this.form.model.id.toString(),
            name: this.form.model.name.toString(),
            status: 'done',
            url: this.previewImage
          }
        ];
      });
    } else {
      this.appsSamlDetailsService.init().subscribe(res => {
        this.form.model.id = res.data.id;
        this.form.model.secret = res.data.secret;
        this.form.model.protocol = res.data.protocol;
      });
    }
    this.cdr.detectChanges();
  }

  handlePreview = async (file: NzUploadFile): Promise<void> => {
    let preview;
    if (!file.url) {
      preview = await getBase64(file.originFileObj!);
    }
    this.previewImage = file.url || preview;
    this.previewVisible = true;
  };

  uploadImageChange(uploadChange: NzUploadChangeParam): void {
    if (uploadChange.file.status === 'done') {
      this.form.model.iconId = uploadChange.file.response.data;
      this.cdr.detectChanges();
    }
  }

  uploadMetaChange(uploadChange: NzUploadChangeParam): void {
    if (uploadChange.file.status === 'done') {
      this.form.model.metaFileId = uploadChange.file.response.data;
      this.cdr.detectChanges();
    }
  }

  onGenerate(e: MouseEvent): void {
    e.preventDefault();
  }

  onGenerateSecret(e: MouseEvent): void {
    e.preventDefault();
    this.appsService.generateSecret('base').subscribe(res => {
      this.form.model.secret = res.data;
      this.cdr.detectChanges();
    });
  }

  onSelectAdapter(e: MouseEvent): void {
    e.preventDefault();
    const modal = this.modalService.create({
      nzContent: SelectAdaptersComponent,
      nzViewContainerRef: this.viewContainerRef,
      nzComponentParams: {
        protocol: 'SAML_v2.0'
      },
      nzOnOk: () => new Promise(resolve => setTimeout(resolve, 1000))
    });
    // Return a result when closed
    modal.afterClose.subscribe(result => {
      if (result.refresh) {
        this.form.model.adapter = result.data.adapter;
        this.form.model.adapterName = result.data.name;
        this.form.model.adapterId = result.data.id;
      }
    });
  }

  onClose(e: MouseEvent): void {
    e.preventDefault();
    this.modal.destroy({ refresh: false });
  }

  onSubmit(e: MouseEvent): void {
    e.preventDefault();
    this.form.submitting = true;
    this.form.model.trans();
    (this.isEdit ? this.appsSamlDetailsService.update(this.form.model) : this.appsSamlDetailsService.add(this.form.model)).subscribe(
      res => {
        if (res.code == 0) {
          this.msg.success(`提交成功`);
        } else {
          this.msg.success(`提交失败`);
        }
        this.form.submitting = false;
        this.modal.destroy({ refresh: true });
        this.cdr.detectChanges();
      }
    );
  }
}
