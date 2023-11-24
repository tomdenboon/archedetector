<template>
  <div class="w-100">
    <div class="d-flex align-items-center p-5 w-100 flex-column">
      <div>
        <b-button class="m-2" variant="success" @click="$bvModal.show('add-mailing-list')">Add mailing List</b-button>
      </div>
      <div class="w-100">
        <b-table class="w-100 border" fixed striped hover :fields="fields" :items="mailingLists" >
          <template #cell(actions)="data">
            <b-button pill size="sm" variant="danger" class="mb-1" @click="deleteMailingList(data.index, data.item.id)">
              Delete <b-icon icon="trash-fill" aria-hidden="true"></b-icon>
            </b-button>
            <b-button pill size="sm" disabled class="mb-1">
              Edit <b-icon icon="pencil" aria-hidden="true"></b-icon>
            </b-button>
          </template>
        </b-table>
      </div>
    </div>
    <b-modal id="add-mailing-list" hide-footer hide-header>
      <b-form>
        <b-form-group id="input-group-1" label="Mailing list name:" label-for="input-1">
          <b-form-input
              id="input-1"
              v-model="form.name"
              placeholder="Enter name"
              required
          ></b-form-input>
        </b-form-group>
        <b-form-group id="input-group-2">
          <label>
            Url to list in
            <a href="http://mail-archives.apache.org/mod_mbox/">mail archive</a>
          </label>
          <b-form-input
              id="input-2"
              v-model="form.url"
              placeholder="Enter url"
              required
          ></b-form-input>
        </b-form-group>
        <input type="checkbox" id="git" value="git" v-model="form.git">
        <label for="git">Github</label>
        <br>
        <input type="checkbox" id="jira" value="jira" v-model="form.jira">
        <label for="jira">Jira</label>
        <br>
      </b-form>
      <b-button class="mt-3" block variant="danger" @click="onCancel">cancel</b-button>
      <b-button class="mt-3" block variant="success" @click="onSubmit">submit</b-button>
    </b-modal>
  </div>
</template>

<script>
import axios from "axios";

const url = process.env.VUE_APP_ARCHEDETECOR_API

export default {
  name: "ManageMailingList",
  data() {
    return {
      form: {
        name: '',
        url: '',
        git: false,
        jira: false
      },
      fields: [
        {
          key: 'name',
          sortable: false
        },
        {
          key: 'url',
          sortable: false
        },
        {
          key: 'size',
          sortable: false
        },
        {
          key: 'actions',
          sortable: false
        }
      ],
      tempMailingLists: [],
      mailingLists: []
    }

  },
  async created(){
    try {
      axios.get(url + "mailing-list/").then((response) => {
        this.tempMailingLists = response.data;
        let promises = []
        for(let i = 0 ; i < this.tempMailingLists.length ; i++){
          promises.push(axios.get(url + "mailing-list/" + this.tempMailingLists[i].id + "/email?size=0").then((res) => {
            this.tempMailingLists[i].size = res.data.totalElements;
          }))
        }
        Promise.all(promises).then(() => {
          this.mailingLists = this.tempMailingLists
        });

      })

    } catch (e) {
      console.error(e)
    }
  },
  methods: {
    deleteMailingList(index, id){
      if(confirm("Are you sure you want to delete this list?")){
        axios.delete(url + "mailing-list/" + id).then(() => {
              this.mailingLists.splice(index, 1)
        }).catch(error => {
              console.log(error);
        })
      }
    },
    onSubmit() {
      this.$bvModal.hide('add-mailing-list')
      let filter = "?filters=";
      if(this.form.git && this.form.jira){
        filter += "git,jira"
      } else if(this.form.jira){
        filter += "jira"
      } else if(this.form.git){
        filter += "git"
      }
      axios.post(url + "mailing-list/add-from-apache-archive" + filter, this.form).then((response) => {
        axios.get(url + "mailing-list/" + response.data.id + "/email?size=0").then((res) => {
          response.data.size = res.data.totalElements;
          this.mailingLists.push(response.data)
        })

      }, (error) => {
        console.log(error);
      });
      this.form.url = ''
      this.form.name = ''
      this.form.git = false
      this.form.jira = false

    },
    onCancel() {
      this.$bvModal.hide('add-mailing-list')
    }
  }

}
</script>

<style scoped>

</style>