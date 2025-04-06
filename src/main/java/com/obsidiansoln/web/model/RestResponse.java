/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.web.model;

public class RestResponse {
	private boolean success;
	private ToastMessage toast;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public ToastMessage getToast() {
		return toast;
	}
	public void setToast(ToastMessage toast) {
		this.toast = toast;
	}
	
	
}
