# TenderFlex

Application was build based on business rules provided by Exadel poland.

The main functions of the application
1. Registration/Authorization/Logout
2. Contractor creates tenders with attached files
3. Bidder creates offer for specific tender
4. Admin view list of users

Registration
To register, the user must enter and choose his role:
* email
* password
* username
* role (contractor/bidder)

Authorization
To log in, the user must enter:
* email
* password

Create new tender
Available after authorization as contractor.
1. Contractor details:
* The user fills the company details
2. Contact person details:
* The user fills contact person details
3. Tender data with 3 files attached

Create new offer for specific tender
Available after authorization as bidder.
1. Contractor details:
* The user fills the company details
2. Contact person details:
* The user fills contact person details
3. Offer data with file attached

Contractor page
Available after authorization as contractor.
1. View of published tenders of this contractor
2. View of offers per each tender

Bidder page
Available after authorization as bidder.
1. View of all published tenders
2. View of published offers of this bidder
3. View of made offer per each tender

# Presentation

For demonstration, queries from the Postman collection from the \postman folder can be run.