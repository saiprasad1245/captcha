import { HostListener, Component } from '@angular/core';
import { OrderService } from '../_services/order.service';
import { TokenStorageService } from '../_services/token-storage.service';

declare var Razorpay: any;

@Component({
selector: 'app-order',
templateUrl: './order.component.html',
styleUrls: ['./order.component.css']
})
export class OrderComponent {

	form: any = {}; 
	paymentId: string;
	error: string;
    private roles: string[];
  isLoggedIn = false;
  showAdminBoard = false;
  showModeratorBoard = false;
  username: string;
	amount: any;
	constructor(private orderService: OrderService,private tokenStorageService: TokenStorageService) {

	}

	ngOnInit() {
	this.isLoggedIn = this.tokenStorageService.getUser();
    console.log("this.isLoggedIn"+this.isLoggedIn);
        if (this.isLoggedIn) {
          const user = this.tokenStorageService.getUser();
          this.roles = user.roles;
    
          this.showAdminBoard = this.roles.includes('ROLE_ADMIN');
          this.showModeratorBoard = this.roles.includes('ROLE_MODERATOR');
    
          this.username = user.displayName;
		  this.amount = user.refundAmount;

          console.log("this.user.refundAmount"+user.refundAmount);
      
        }
	}

	options = {
	"key": "",
	"amount": "", 
	"name": "Sai Prasad",
	"description": "Web Development",
	"image": "https://www.captcha.com/wp-content/uploads/2020/02/android-chrome-512x512-1.png",
	"order_id":"",
	"handler": function (response){
		var event = new CustomEvent("payment.success", 
			{
				detail: response,
				bubbles: true,
				cancelable: true
			}
		);	  
		window.dispatchEvent(event);
	}
	,
	"prefill": {
	"name": "",
	"email": "",
	"contact": ""
	},
	"notes": {
	"address": ""
	},
	"theme": {
	"color": "#3399cc"
	}
	};

	onSubmit(): void {
		this.paymentId = ''; 
		this.error = ''; 
		this.orderService.createOrder(this.form,this.username).subscribe(
		data => {
			this.options.key = data.secretKey;
			this.options.order_id = data.razorpayOrderId;
			this.options.amount = data.applicationFee; //paise
			this.options.prefill.name = this.form.name;
			this.options.prefill.email = this.form.email;
			this.options.prefill.contact = this.form.phone;
			var rzp1 = new Razorpay(this.options);
			rzp1.open();
				      
			rzp1.on('payment.failed', function (response){    
				// Todo - store this information in the server
				console.log(response.error.code);    
				console.log(response.error.description);    
				console.log(response.error.source);    
				console.log(response.error.step);    
				console.log(response.error.reason);    
				console.log(response.error.metadata.order_id);    
				console.log(response.error.metadata.payment_id);
				this.error = response.error.reason;
			}
			);
		}
		,
		err => {
			this.error = err.error.message;
		}
		);
	}

	@HostListener('window:payment.success', ['$event']) 
	onPaymentSuccess(event): void {
		this.orderService.updateOrder(event.detail,this.username,this.form.amount).subscribe(
		data => {
			this.paymentId = data.message;
		}
		,
		err => {
			this.error = err.error.message;
		}
		);
	}
}
