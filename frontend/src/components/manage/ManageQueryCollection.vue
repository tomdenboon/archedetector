<template>
  <div class="d-flex flex-column align-items-center w-100  me-5 ms-5 mt-4">
    <h3>Query Collections</h3>
    <b-card header-tag="header" class="w-100">
      <template #header>
        <h5 >Add a new query collection</h5>
      </template>
      <b-form>
        <b-row>
          <b-col align-self="center">
            <b-row class="d-flex pb-4">
              <label>Name:</label>
              <b-input-group class="ms-1 ">
                <b-form-input id="inline-form-input-username" v-model="queryCollection.name"></b-form-input>
              </b-input-group>
            </b-row>
            <b-row class="d-flex pb-4">
              <label>Id:</label>
              <b-input-group class="ms-1 ">
                <b-form-input disabled id="inline-form-input-username" v-model="queryCollection.id"></b-form-input>
              </b-input-group>
            </b-row>
          </b-col>
          <b-col>
            <b-row class="d-flex mx-2">
              <label>Select mailing lists:</label>
              <b-form-checkbox-group
                  class="border w-100"
                  style="height: 200px; overflow: auto"
                  id="checkbox-group-2"
                  v-model="queryCollection.mailingLists">
                <b-form-checkbox v-for="mailingList in mailingLists" :key="mailingList.id" :value="mailingList">
                  {{mailingList.name}}
                </b-form-checkbox>
              </b-form-checkbox-group>
            </b-row>
          </b-col>
          <b-col>
            <b-row class="d-flex mx-2">
              <label>Select issue lists:</label>
              <b-form-checkbox-group
                  class="border w-100"
                  style="height: 200px; overflow: auto"
                  id="checkbox-group-2"
                  v-model="queryCollection.issueLists">
                <b-form-checkbox v-for="issueList in issueLists" :key="issueList.id" :value="issueList">
                  {{issueList.name}}
                </b-form-checkbox>
              </b-form-checkbox-group>
            </b-row>
          </b-col>
          <b-col align-self="center">
            <b-button class="w-50" variant="success" @click="saveQueryCollection">
              Save
            </b-button>
          </b-col>
        </b-row>
      </b-form>
    </b-card>
    <b-card header-tag="header" class="w-100 mt-5">
      <template #header>
        <h5 >Query Collection List</h5>
      </template>
      <b-table class="w-100 border-top" fixed hover :fields="fields" :items="queryCollections" >
        <template #cell(actions)="data">
          <b-button size="sm" pill variant="danger" class="mb-1" @click="deleteQueryCollection(data.index, data.item.id)">
            Delete <b-icon icon="trash-fill" aria-hidden="true"></b-icon>
          </b-button>
          <b-button size="sm"  pill class="mb-1" @click="editQueryCollection(data.item)">
            Edit <b-icon icon="pencil" aria-hidden="true"></b-icon>
          </b-button>
        </template>
      </b-table>
    </b-card>
  </div>
</template>

<script>
import axios from "axios";

const url = process.env.VUE_APP_ARCHEDETECOR_API

export default {
  name: "ManageQueryCollection",
  data() {
    return {
      fields: [
        {
          key: 'name',
          sortable: false
        },
        {
          key: 'actions',
          sortable: false
        }
      ],
      queryCollection: {
        id: "",
        name: "",
        mailingLists: [],
        issueLists: [],
      },
      issueLists: [],
      mailingLists: [],
      queryCollections: []
    }
  },
  async created(){
    try {
      axios.get(url + "query-collection").then((response) => {
        this.queryCollections = response.data;
      })
    } catch (e) {
      console.error(e)
    }
    try {
      axios.get(url + "issue-list").then((response) => {
        this.issueLists = response.data;
      })
    } catch (e) {
      console.error(e)
    }
    try {
      axios.get(url + "mailing-list").then((response) => {
        this.mailingLists = response.data;
      })
    } catch (e) {
      console.error(e)
    }
  },
  methods: {
    saveQueryCollection(){
      axios.post(url + "query-collection", this.queryCollection).then((response) => {
        let idx = -1
        for(let i = 0; i < this.queryCollections.length; i++){
          if(this.queryCollections[i].id === response.data.id){
            idx = i;
          }
        }
        if(idx > -1){
          this.queryCollections[idx].id = response.data.id
          this.queryCollections[idx].name = response.data.name
          this.queryCollections[idx].mailingLists = response.data.mailingLists
          this.queryCollections[idx].issueLists = response.data.issueLists
        } else {
          this.queryCollections.push(response.data)
        }

      }, (error) => {
        console.log(error);
      });
      this.queryCollection.id = ""
      this.queryCollection.name = ""
      this.queryCollection.mailingLists = []
      this.queryCollection.issueLists = []
    },
    deleteQueryCollection(index, id){
      if(confirm("Are you sure you want to delete this list?")){
        axios.delete(url + "query-collection/" + id).then(() => {
          this.queryCollections.splice(index, 1)
        }).catch(error => {
          console.log(error);
        })
      }
    },
    editQueryCollection(queryCollection){
      this.queryCollection.id = queryCollection.id
      this.queryCollection.name = queryCollection.name
      this.queryCollection.mailingLists = queryCollection.mailingLists
      this.queryCollection.issueLists = queryCollection.issueLists
    }
  }
}
</script>

<style scoped>

</style>