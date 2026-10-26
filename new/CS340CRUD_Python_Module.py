# Example Python Code to Insert a Document 

from pymongo import MongoClient 
from bson.objectid import ObjectId 

class AnimalShelter(object): 
    """ CRUD operations for Animal collection in MongoDB """ 

    def __init__(self): 
        # Initializing the MongoClient. This helps to access the MongoDB 
        # databases and collections. This is hard-wired to use the aac 
        # database, the animals collection, and the aac user. 
        # Connection Variables 
        #
        USER = 'aacuser' 
        PASS = '1234baby' 
        HOST = 'localhost' 
        PORT = 27017 
        DB = 'aac' 
        COL = 'animals' 
        # 
        # Initialize Connection 
        # 
        self.client = MongoClient('mongodb://%s:%s@%s:%d' % (USER,PASS,HOST,PORT)) 
        self.database = self.client['%s' % (DB)] 
        self.collection = self.database['%s' % (COL)] 
            
    # Method to implement the C in CRUD. 
    def create(self, data):
        if data is not None:
            try:
                self.database.animals.insert_one(data)  # data should be dictionary  
                return True
            except Exception as e:
                print("Create failed:", e)
                return False
        else: 
            raise Exception("Nothing to save, because data parameter is empty") 

    # Method to implement the R in CRUD.
    def read(self, query):
        if query is not None:
            try:
                data = self.database.animals.find(query)
                return list(data)
            except Exception as e:
                print("Read failed:", e)
                return[]
        else:
            raise Exception("Nothing to read, because query is empty") 
                
    # Method to implement the U in CRUD.
    def update(self, query, newData):
        if query is not None and newData is not None:
            try:
                result = self.database.animals.update_many(
                    query,
                    {"$set": newData}
                )
                return result.modified_count 
            except Exception as e:
                print("Update failed:", e)
                return 0
        else:
            raise Exception("Nothing to update, because query or newData is empty") 
        
    # Method to implement the D in CRUD.
    def delete(self, query):
        if query is not None:
            try:
                result = self.database.animals.delete_many(query)
                return result.deleted_count
            except Exception as e:
                print("Delete failed:", e)
                return 0
        else:
            raise Exception("Nothing to delete, because query is empty") 