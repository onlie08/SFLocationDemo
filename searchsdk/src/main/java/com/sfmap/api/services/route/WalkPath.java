package com.sfmap.api.services.route;

import java.util.ArrayList;
import java.util.List;

/**
 * 定义了步行路径规划的一个方案。
 */
public class WalkPath extends Path implements android.os.Parcelable {
	public static final Creator<WalkPath> CREATOR = new WalkPathCreator();;
	private List<WalkStep> steps = new ArrayList<WalkStep>();

	/**
	 * WalkPath的构造方法。
	 */
	public WalkPath() {
		super();
	}

	/**
	 * 序列化实现。
	 * @param source Parcel对象。
	 */
	public WalkPath(android.os.Parcel source) {
		super(source);
		this.steps = source.createTypedArrayList(WalkStep.CREATOR);
	}

	/**
	 * 返回步行方案的路段列表。
	 * 
	 * @return 路段列表.
	 */
	public List<WalkStep> getSteps() {
		return this.steps;
	}

	/**
	 * 设置步行方案的路段列表。
	 * 
	 * @param mSteps
	 *            路段列表。
	 */
	public void setSteps(List<WalkStep> mSteps) {
		this.steps = mSteps;
	}

	@Override
	public void writeToParcel(android.os.Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeTypedList(this.steps);
	}
}
