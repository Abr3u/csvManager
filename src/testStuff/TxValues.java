package testStuff;

public class TxValues {
	public String id;
	public String createdDate;
	public String createdBy;
	public String description;
	public String docNumber;
	public String accountId;
	public String accountType;
	public String period;
	
	public TxValues(String id, String createdDate, String createdBy, String description, String docNumber, String accountId, String accountType, String period) {
		this.id = id;
		this.createdDate = createdDate;
		this.createdBy = createdBy;
		this.description = description;
		this.docNumber = docNumber;
		this.accountId = accountId;
		this.accountType = accountType;
		this.period = period;
	}
}
