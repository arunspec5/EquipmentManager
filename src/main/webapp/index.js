var app = new Vue({ 
    el: '#app',
    data: {
    	items:[
        
        	],
        equipment:{
        	equipmentNumber:'',
        	address:'',
        	contractStartDate:'',
        	contractEndDate:'',
        	status:''
        },
        limit:'',
        number:'',
        postMessage:'',
        postFailMessage:'',
        failureMessage:''
        
    },
    methods:{
    button: function() {
    	this.failureMessage =''
    	axios.get("./EquipmentManager/equipment/"+this.number)
        .then(response => {this.items =[]
        this.$set(this.items, 0, response.data)
        
        }).catch(error => {
    		this.failureMessage = error.response.data})
      },
    lists: function() {
    	this.failureMessage =''
    	axios.get("./EquipmentManager/equipment/search?limit="+this.limit)
        .then(response => {
        	this.items = response.data
        	}).catch(error => {
        		this.failureMessage = error.response.data
        	})
    },
    posts:function() {
    	this.postMessage=''
    	this.postFailMessage=''
    	axios.post("./EquipmentManager/equipment", this.equipment, { headers: {
    	      'Content-type': 'application/json'
        }
      }).then(response => {
    		this.postMessage = "Created Successfully"
    	}).catch(error => {
    		this.postFailMessage = error.response.data
    		if(error.response.data.includes("com.fasterxml.jackson.databind.exc.InvalidFormatException")){
    			this.postFailMessage = "Please provide Status"
    		}
    	})
    }
}
});