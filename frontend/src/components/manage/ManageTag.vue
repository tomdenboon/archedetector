<template>
  <div class="d-flex flex-column align-items-center w-100  me-5 ms-5 mt-4">
    <h3>Tags</h3>
    <b-card header-tag="header" class="w-100">
      <template #header>
        <h5 >Add a new tag</h5>
      </template>
      <b-form>
        <b-row>
          <b-col align-self="center">
            <b-row class="d-flex pb-4">
              <label>Id:</label>
              <b-input-group class="ms-1 ">
                <b-form-input disabled id="inline-form-input-username" v-model="tag.id"></b-form-input>
              </b-input-group>
            </b-row>
          </b-col>
          <b-col align-self="center">
            <b-row class="d-flex pb-4">
              <label>Name:</label>
              <b-input-group class="ms-1 ">
                <b-form-input id="inline-form-input-username" v-model="tag.name"></b-form-input>
              </b-input-group>
            </b-row>
          </b-col>
          <b-col align-self="center">
            <b-button class="w-50" variant="success" @click="saveTag">
              Save
            </b-button>
          </b-col>
        </b-row>
      </b-form>
    </b-card>
    <b-card header-tag="header" class="w-100 mt-5">
      <template #header>
        <h5 >Tag List</h5>
      </template>
      <b-table class="w-100 border-top" fixed hover :fields="fields" :items="tags" >
        <template #cell(actions)="data">
          <b-button size="sm" pill variant="danger" class="mb-1" @click="deleteTag(data.index, data.item.id)">
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
  name: "ManageTag",
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
      tag: {
        id: "",
        name: ""
      },
      tags: []
    }
  },
  async created(){
    try {
      axios.get(url + "tag").then((response) => {
        this.tags = response.data;
      })
    } catch (e) {
      console.error(e)
    }
  },
  methods: {
    saveTag(){
      console.log(url)
      axios.post(url + "tag", this.tag).then((response) => {
        let idx = -1
        for(let i = 0; i < this.tags.length; i++){
          if(this.tags[i].id === response.data.id){
            idx = i;
          }
        }
        if(idx > -1){
          this.tags[idx].id = response.data.id
          this.tags[idx].name = response.data.name
        } else {
          this.tags.push(response.data)
        }
      }, (error) => {
        console.log(error);
      });
      this.tag.id = ""
      this.tag.name = ""
    },
    deleteTag(index, id){
      if(confirm("Are you sure you want to delete this list?")){
        axios.delete(url + "tag/" + id).then(() => {
          this.tags.splice(index, 1)
        }).catch(error => {
          console.log(error);
        })
      }
    },
    editQueryCollection(tag){
      this.tag.id = tag.id
      this.tag.name = tag.name
    }
  }
}
</script>

<style scoped>

</style>