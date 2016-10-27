package uk.co.sample.project.domain;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Type;

@MappedSuperclass
public class GenericDomain implements Serializable {

	private static final long serialVersionUID = -4895405150030740839L;
	private Long mPk;
	private Integer mVersion;
	private LocalDateTime mLastUpdated;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PK", nullable = false)
	public Long getPk() {
		return mPk;
	}

	public void setPk(Long aPk) {
		mPk = aPk;
	}

	@Version
	@Column(name = "VERSION")
	public Integer getVersion() {
		return mVersion;
	}

	public void setVersion(Integer aVersion) {
		mVersion = aVersion;
	}

	@Column(name = "LAST_UPDATED")
	@Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
	public LocalDateTime getLastUpdated() {
		return mLastUpdated;
	}

	public void setLastUpdated(LocalDateTime aLastUpdated) {
		mLastUpdated = aLastUpdated;
	}

	@PrePersist
	protected void onCreate() {
		mLastUpdated = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		mLastUpdated = LocalDateTime.now();
	}

	@Transient
	public String byteToString(byte[] aContent) {
		return new String(aContent, StandardCharsets.UTF_8);
	}

	@Transient
	public byte[] stringToByte(String aContent) {
		return aContent.getBytes(StandardCharsets.UTF_8);
	}
}
