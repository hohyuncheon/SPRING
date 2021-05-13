package com.kh.spring.tv.model.vo;

public class SamsungTv implements Tv {

	
	private RemoteControl remocon;
	
	
	/*
	 * bean>property
	 * setter를 이용해서 의존주입 Ddependency Injection한다
	 * 
	 */
	public void setRemocon(RemoteControl remocon) {
		System.out.println("setRemocon"+ remocon);
		this.remocon = remocon;
		
	}
	/*
	 * bean>constructor
	 * 생성자를 이용해서 의존주입 DI한다
	 * 
	 */
	public SamsungTv(RemoteControl remocon) {
		System.out.println("LgTv 객체생성"+ remocon);
		this.remocon= remocon;
	}
	
	
	public SamsungTv() {
		System.out.println("SamsungTv객체를 생성했습니다.");
	}
	
	@Override
	public void powerOn() {
		System.out.println("SamsungTv의 전원을 켰습니다.");
	}
	
	
	public void changeChannel(int no) {
		this.remocon.changeChannel(no);
	}

}