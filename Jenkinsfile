/* Declarative Pipeline */
pipeline {
	/* select target host by label for building this pipeline */
	agent {
  		label 'AutoTest'
	}


	parameters {
  		booleanParam name: 'Debug', defaultValue: false, description: 'debug mode - not delete the workspace at the end'
	}

	/* Trigger to start pipeline - every 5 minutes look for SCM changes
	triggers {
        pollSCM('H/5 * * * 1-5')
    }
    */

	/* Stages to run for this pipeline */
	stages {

		stage('Preparation') {
			steps {
				script {
				/*	git 'https://dev.azure.com/GilatDevOps/Automation-Testing/_git/SW_Upgrade_HE'
                */
                git credentialsId: 'd66e51c5-4b72-4864-8d26-972d1416800a', url: 'https://dev.azure.com/GilatDevOps/Automation-Testing/_git/SW_Upgrade_HE'
			     }
			}
		}

		stage('Build') {
			steps {
				script {

					/*sh "cd gRPC_UMS"*/
					sh "chmod +x build*"

					sh "./build.sh"
					sh "./buildAutoTestPack.sh"

				}
			}
		}

		stage('Publish') {
			steps {
				script {
						sh 'chmod +x addVersion.sh'
						sh './addVersion.sh'

				}
			}
		}
	}
}