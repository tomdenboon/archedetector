<template>
  <div class="w-100">
    <div class="d-flex align-items-center w-100 flex-column p-5">
      <div>
        <b-button class="m-2" variant="success" @click="$bvModal.show('add-issue-list')">Add issue List</b-button>
      </div>
      <div class="w-100" >
        <b-table class="w-100 border" striped fixed hover :fields="fields" :items="issueLists" >
          <template #cell(actions)="data">
            <b-button size="sm" pill variant="danger" class="mb-1" @click="deleteIssueList(data.index, data.item.id)">
              Delete <b-icon icon="trash-fill" aria-hidden="true"></b-icon>
            </b-button>
            <b-button size="sm" pill disabled class="mb-1">
              Edit <b-icon icon="pencil" aria-hidden="true"></b-icon>
            </b-button>
          </template>
        </b-table>
      </div>
    </div>
    <b-modal id="add-issue-list" hide-footer hide-header>
      <b-form>
        <b-form-group id="input-group-1" label="Project name:" label-for="input-1">
          <b-form-input
              id="input-1"
              v-model="form.issue.name"
              placeholder="Enter name"
              required
          ></b-form-input>
        </b-form-group>
        <b-form-group id="input-group-2">
          <label>
            Project key from
            <a href="https://issues.apache.org/jira/secure/BrowseProjects.jspa?selectedCategory=all&selectedProjectType=software">apache issues</a>
          </label>
          <b-form-input
              id="input2"
              v-model="form.issue.key"
              placeholder="Enter key"
              required
          ></b-form-input>
        </b-form-group>
        <b-form-group id="input-group-3">
          <label>
            Comma separated users to filter out
          </label>
          <b-form-input
              id="input2"
              v-model="form.filterUsers"
              placeholder="Hudson,Avinash Lakshman,Jonathan Ellis"
              required
          ></b-form-input>
        </b-form-group>
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
  name: "ManageIssueList",
  data() {
    return {
      form: {
        issue: {
          name: '',
          key: ''
        },
        filterUsers: ''
      },
      fields: [
        {
          key: 'name',
          sortable: false
        },
        {
          key: 'key',
          sortable: false
        },
        {
          key: 'actions',
          sortable: false
        }
      ],
      issueLists: []
    }

  },
  async created(){
    try {
      axios.get(url + "issue-list").then((response) => {
        this.issueLists = response.data;
      })
    } catch (e) {
      console.error(e)
    }
  },
  methods: {
    deleteIssueList(index, id){
      if(confirm("Are you sure you want to delete this list?")){
        axios.delete(url + "issue-list/" + id).then(() => {
          this.issueLists.splice(index, 1)
        }).catch(error => {
          console.log(error);
        })
      }
    },
    onSubmit() {
      this.$bvModal.hide('add-issue-list')
      if(this.form.filterUsers.length > 0) {
        axios.post(url + "issue-list/add-from-apache-issues?filterUsers=" + this.form.filterUsers, this.form.issue).then((response) => {
          this.issueLists.push(response.data)
        }, (error) => {
          console.log(error);
        });
      } else {
        axios.post(url + "issue-list/add-from-apache-issues", this.form.issue).then((response) => {
          this.issueLists.push(response.data)
        }, (error) => {
          console.log(error);
        });
      }
      this.form.issue.key = ''
      this.form.issue.name = ''
    },
    onCancel() {
      this.$bvModal.hide('add-issue-list')
    }
  }

}
</script>

<style scoped>

</style>