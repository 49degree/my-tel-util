package com.custom.bean;

import com.custom.utils.Logger;
import com.custom.view.SecondViewGroup;

public class PageNumBean {
	private static final Logger logger = Logger.getLogger(PageNumBean.class);
	
	private int buttonPerPage = 8;
	private int pageNumPerView  = 5;
	
	private int buttonCount = 0;
	private int pageCount = 0;
	
	private int curPageNum = 0;
	private int pageViewCount = 0;
	private int pageViewIndex = 0;
	
	private int startPageNum = 0;
	private int endPageNum = 0;
	
	private int startButtonIndex = 0;
	private int endButtonIndex = 0;
	
	
	public PageNumBean(int buttonCount){
		this.buttonCount = buttonCount;
		pageCount = buttonCount%buttonPerPage>0? buttonCount/buttonPerPage+1: buttonCount/buttonPerPage;
		pageViewCount = pageCount%pageNumPerView>0?pageCount/pageNumPerView+1:pageCount/pageNumPerView;
		
		
		curPageNum = 0;
		startPageNum = 0;
		endPageNum = pageNumPerView<pageCount?pageNumPerView-1:pageCount-1;
		pageViewIndex = 0;
		
		startButtonIndex = 0;
		endButtonIndex = buttonPerPage<buttonCount?(buttonPerPage-1):(buttonCount-1);
	}
	/**
	 * 根据页码计算开始和结束按钮位置
	 * @param pageNum
	 * @return
	 */
	public int[] getButtonIndexbyPageNum(int pageNum){
		if(pageNum>=pageCount||pageNum<0){
			return null;
		}
		int startIndex = pageNum*buttonPerPage;
		int endIndex = (startIndex+buttonPerPage)<buttonCount?(startIndex+buttonPerPage-1):buttonCount-1;
		return new int[]{startIndex,endIndex};
	}
	
	/**
	 * 设置显示的页码
	 * @param curPageNum
	 * @return
	 */
	public boolean setCurPageNum(int curPageNum) {
		logger.error("this.curPageNum:"+this.curPageNum);
		if(curPageNum>=pageCount||curPageNum<0){
			return false;
		}
		logger.error("curPageNum:"+curPageNum);
		this.curPageNum = curPageNum;
		startPageNum = (curPageNum/pageNumPerView)*pageNumPerView;
		endPageNum = (startPageNum+pageNumPerView-1)<(pageCount-1)?(startPageNum+pageNumPerView-1):(pageCount-1);
		pageViewIndex = startPageNum/pageNumPerView;
		
		
		startButtonIndex = curPageNum*buttonPerPage;
		endButtonIndex = (startButtonIndex+buttonPerPage)<buttonCount? (startButtonIndex+buttonPerPage-1):buttonCount-1;
		return true;
	}
	
	public boolean nextPageNum() {
		return setCurPageNum(curPageNum+1);
	}
	
	public boolean prePageNum() {
		return setCurPageNum(curPageNum-1);
	}
	/**
	 * 设置显示第几组页面
	 * @param pageViewIndex
	 * @return
	 */
	public boolean setPageViewIndex(int mPageViewIndex) {
		if(mPageViewIndex>=pageViewCount||mPageViewIndex<0||this.pageViewIndex == mPageViewIndex){
			return false;
		}
		if(this.pageViewIndex>mPageViewIndex){
			curPageNum = (mPageViewIndex+1)*pageNumPerView-1;
		}else{
			curPageNum = mPageViewIndex*pageNumPerView;
		}
		this.pageViewIndex = mPageViewIndex;
		return setCurPageNum(curPageNum);
	}
	
	public boolean nextPageView() {
		return setPageViewIndex(pageViewIndex+1);
	}
	
	public boolean prePageView() {
		return setPageViewIndex(pageViewIndex-1);
	}
	
	
	
	public int getEndPageNum() {
		return endPageNum;
	}
	public int getEndButtonIndex() {
		return endButtonIndex;
	}
	public int getStartButtonIndex() {
		return startButtonIndex;
	}

	public int getPageCount() {
		return pageCount;
	}

	public int getCurPageNum() {
		return curPageNum;
	}

	public int getButtonCount() {
		return buttonCount;
	}
	public void setButtonCount(int buttonCount) {
		this.buttonCount = buttonCount;
	}
	public int getButtonPerPage() {
		return buttonPerPage;
	}
	public void setButtonPerPage(int buttonPerPage) {
		this.buttonPerPage = buttonPerPage;
	}
	public int getStartPageNum() {
		return startPageNum;
	}
	public void setStartPageNum(int startPageNum) {
		this.startPageNum = startPageNum;
	}
	public int getPageNumPerView() {
		return pageNumPerView;
	}
	public void setPageNumPerView(int pageNumPerView) {
		this.pageNumPerView = pageNumPerView;
	}
	
	
}
