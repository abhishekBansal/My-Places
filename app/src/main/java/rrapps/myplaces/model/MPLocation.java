package rrapps.myplaces.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.internal.LongHashMap;

/**
 *
 * Created by abhishek on 08/08/15.
 */

@Entity
public class MPLocation implements Parcelable {
    private String name;

    /**
     * Unique Id for this location
     */
    @Id(autoincrement = true)
    private
    Long id;

    @NotNull
    private
    double latitude;

    @NotNull
    private
    double longitude;

    private String address;

    private String group;

    private boolean isParkedCar;

    protected MPLocation(Parcel in) {
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        address = in.readString();
        group = in.readString();
        isParkedCar = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(address);
        dest.writeString(group);
        dest.writeByte((byte) (isParkedCar ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MPLocation> CREATOR = new Creator<MPLocation>() {
        @Override
        public MPLocation createFromParcel(Parcel in) {
            return new MPLocation(in);
        }

        @Override
        public MPLocation[] newArray(int size) {
            return new MPLocation[size];
        }
    };

    @Override
    public String toString() {
        return name;
    }

    @Generated(hash = 47042032)
    public MPLocation(String name, Long id, double latitude, double longitude,
            String address, String group, boolean isParkedCar) {
        this.name = name;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.group = group;
        this.isParkedCar = isParkedCar;
    }

    @Generated(hash = 1966305554)
    public MPLocation() {
    }

    private void readFromParcel(Parcel in) {
        name = in.readString();
        id = in.readLong();
        latitude = in.readDouble();
        longitude = in.readDouble();
        group = in.readString();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isParkedCar() {
        return isParkedCar;
    }

    public void setParkedCar(boolean parkedCar) {
        isParkedCar = parkedCar;
    }

    public boolean getIsParkedCar() {
        return this.isParkedCar;
    }

    public void setIsParkedCar(boolean isParkedCar) {
        this.isParkedCar = isParkedCar;
    }

}