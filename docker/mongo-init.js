// Initialize MongoDB with users and collections
db = db.getSiblingDB('plagiarism_db');

// Create application user
db.createUser({
  user: "plagiarism_user",
  pwd: "plagiarism_pass",
  roles: [
    { role: "readWrite", db: "plagiarism_db" },
    { role: "dbAdmin", db: "plagiarism_db" }
  ]
});

// Create collections
db.createCollection("users");
db.createCollection("jobs");
db.createCollection("submissions");
db.createCollection("similarity_results");
db.createCollection("audit_logs");

// Create indexes
db.users.createIndex({ "email": 1 }, { unique: true });
db.jobs.createIndex({ "user_id": 1 });
db.jobs.createIndex({ "status": 1 });
db.submissions.createIndex({ "job_id": 1 });
db.submissions.createIndex({ "student_name": 1 });
db.similarity_results.createIndex({ "job_id": 1 });
db.similarity_results.createIndex({ "similarity_percentage": -1 });