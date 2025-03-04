import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { _HttpClient } from '@delon/theme';
import { MatchControl } from '@delon/util/form';
import { NzSafeAny } from 'ng-zorro-antd/core/types';
import { NzMessageService } from 'ng-zorro-antd/message';
import { finalize } from 'rxjs/operators';

import { SignUpService } from '../../../service/signup.service';

@Component({
  selector: 'passport-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.less'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserRegisterComponent implements OnDestroy {
  constructor(
    fb: FormBuilder,
    private signUpService: SignUpService,
    private msg: NzMessageService,
    private router: Router,
    private http: _HttpClient,
    private cdr: ChangeDetectorRef
  ) {
    this.form = fb.group(
      {
        username: [null, [Validators.required]],
        displayName: [null, [Validators.required]],
        email: [null, [Validators.required, Validators.email]],
        password: [null, [Validators.required, Validators.minLength(6), UserRegisterComponent.checkPassword.bind(this)]],
        confirm: [null, [Validators.required, Validators.minLength(6)]],
        mobilePrefix: ['+86'],
        mobile: [null, [Validators.required, Validators.pattern(/^1\d{10}$/)]],
        captcha: [null, [Validators.required]]
      },
      {
        validators: MatchControl('password', 'confirm')
      }
    );
  }

  // #region fields

  get username(): AbstractControl {
    return this.form.get('username')!;
  }

  get displayName(): AbstractControl {
    return this.form.get('displayName')!;
  }
  get email(): AbstractControl {
    return this.form.get('email')!;
  }
  get password(): AbstractControl {
    return this.form.get('password')!;
  }
  get confirm(): AbstractControl {
    return this.form.get('confirm')!;
  }
  get mobile(): AbstractControl {
    return this.form.get('mobile')!;
  }
  get captcha(): AbstractControl {
    return this.form.get('captcha')!;
  }
  form: FormGroup;
  error = '';
  type = 0;
  loading = false;
  captchaLoading = false;
  visible = false;
  status = 'pool';
  progress = 0;
  passwordProgressMap: { [key: string]: 'success' | 'normal' | 'exception' } = {
    ok: 'success',
    pass: 'normal',
    pool: 'exception'
  };

  // #endregion

  // #region get captcha

  count = 0;
  interval$: any;

  static checkPassword(control: FormControl): NzSafeAny {
    if (!control) {
      return null;
    }
    // eslint-disable-next-line @typescript-eslint/no-this-alias
    const self: any = this;
    self.visible = !!control.value;
    if (control.value && control.value.length > 9) {
      self.status = 'ok';
    } else if (control.value && control.value.length > 5) {
      self.status = 'pass';
    } else {
      self.status = 'pool';
    }

    if (self.visible) {
      self.progress = control.value.length * 10 > 100 ? 100 : control.value.length * 10;
    }
  }

  getCaptcha(): void {
    if (this.mobile.invalid) {
      this.mobile.markAsDirty({ onlySelf: true });
      this.mobile.updateValueAndValidity({ onlySelf: true });
      return;
    }
    this.captchaLoading = true;
    this.signUpService
      .produceOtp({ mobile: this.mobile.value })
      .pipe(
        finalize(() => {
          this.captchaLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe(res => {
        if (res.code !== 0) {
          this.msg.success(`短信发送失败`);
          this.cdr.detectChanges();
        }
        this.msg.success(`短信发送成功`);
      });
    this.count = 59;
    this.cdr.detectChanges();
    this.interval$ = setInterval(() => {
      this.count -= 1;
      this.cdr.detectChanges();
      if (this.count <= 0) {
        clearInterval(this.interval$);
      }
    }, 1000);
  }

  // #endregion
  submit(): void {
    this.error = '';
    Object.keys(this.form.controls).forEach(key => {
      this.form.controls[key].markAsDirty();
      this.form.controls[key].updateValueAndValidity();
    });
    if (this.form.invalid) {
      return;
    }

    const data = this.form.value;
    this.loading = true;
    this.cdr.detectChanges();
    this.signUpService
      .register(data)
      .pipe(
        finalize(() => {
          this.loading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe(res => {
        if (res.code !== 0) {
          this.msg.success(`注册失败`);
          this.cdr.detectChanges();
          return;
        }
        this.msg.success(`注册成功`);
        this.router.navigate(['passport', 'register-result'], { queryParams: { email: data.mail } });
      });
  }

  ngOnDestroy(): void {
    if (this.interval$) {
      clearInterval(this.interval$);
    }
  }
}
